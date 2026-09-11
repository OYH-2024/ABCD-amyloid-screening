package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet("/batch-start")
public class CmdProteinsDocking extends HttpServlet {

    private ExecutorService executorService = Executors.newFixedThreadPool(3); // 最多同时3个任务

    // 与 FileUploadServlet 保持一致的基础目录 + 16个靶点子文件夹
    private static final String BASE_DIR = "E:/DataBase/WebSrever/Docking/";
    private static final String[] TARGET_FOLDERS = {
            "1AML", "1BA4", "1IYT", "1XQ8", "1Z0Q", "2BEG", "2M9R", "2NAO",
            "5OQV", "6A6B", "6OC9", "6RHY", "6SHS", "6TI7", "6YEJ", "7SP1"
    };
    private static final List<String> VALID_PROTEINS = Arrays.asList(TARGET_FOLDERS);

    // Python配置常量
    private static final String PYTHON_INTERPRETER = "C:\\Users\\Lenovo\\PycharmProjects\\pythonProject\\venv\\Scripts\\python.exe";
    private static final String PYTHON_SCRIPT = "E:\\DataBase\\PythonCode\\FileProcess.py";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String[] rawProteins = request.getParameterValues("proteins");

        // 只保留合法的16个靶点ID，过滤掉任何异常/伪造的参数（安全兜底）
        List<String> selectedProteins = new ArrayList<>();
        if (rawProteins != null) {
            for (String p : rawProteins) {
                if (VALID_PROTEINS.contains(p)) {
                    selectedProteins.add(p);
                }
            }
        }

        if (selectedProteins.isEmpty()) {
            redirectToError(response, "No protein has been selected");
            return;
        }

        // 检查每个蛋白质的状态
        StringBuilder unavailableProteins = new StringBuilder();
        for (String protein : selectedProteins) {
            if (!isProteinAvailable(protein)) {
                unavailableProteins.append(protein).append(" ");
            }
        }

        if (unavailableProteins.length() > 0) {
            redirectToError(response, "The following proteins are currently running and cannot be started:" + unavailableProteins.toString());
            return;
        }

        // 启动批量对接
        startBatchDocking(selectedProteins);

        // ---- 关键修复：在redirect跳转之前，同步删除每个靶点旧的结果文件 ----
        // 原因：dock.bat是异步在新窗口里启动的，它自己删除旧DockingResults.txt这一步
        // 有可能比网页第一次查询完成状态还慢，导致网页第一次查询时看到的是"上一次遗留的旧文件"，
        // 误判为"已完成"。这里改成由Java同步先清理一遍，保证redirect跳转出去的时候，
        // 旧文件已经确定不存在了，网页查到的必然是"这一轮"真实的状态。
        for (String protein : selectedProteins) {
            clearStaleResultsFile(getWorkDirectory(protein));
        }

        String proteinsParam = String.join(",", selectedProteins);
        response.sendRedirect("docking-batch-status?proteins=" + URLEncoder.encode(proteinsParam, "UTF-8"));
    }

    /**
     * 同步删除某个靶点工作目录下旧的 DockingResults.txt / DockingResults.tmp.txt
     * （如果存在的话），保证下一轮对接的完成状态判断不会被上一轮的遗留文件误导
     */
    private void clearStaleResultsFile(String workDir) {
        File resultsFile = new File(workDir, "DockingResults.txt");
        if (resultsFile.exists()) {
            resultsFile.delete();
        }
        File tempResultsFile = new File(workDir, "DockingResults.tmp.txt");
        if (tempResultsFile.exists()) {
            tempResultsFile.delete();
        }
    }

    private void redirectToError(HttpServletResponse response, String message) throws IOException {
        response.sendRedirect("docking-batch-error?message=" + URLEncoder.encode(message, "UTF-8"));
    }

    private void startBatchDocking(List<String> proteins) {
        for (String protein : proteins) {
            executorService.submit(() -> {
                try {
                    startSingleProteinDocking(protein);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void startSingleProteinDocking(String protein) throws Exception {
        String workDir = getWorkDirectory(protein);
        String command = getDockingCommand(protein);

        // 打开新窗口执行对接
        Runtime rt = Runtime.getRuntime();
        String cmd = String.format("cmd /C start cmd /c \"cd/d %s && %s\"", workDir, command);
        Process pro = rt.exec(cmd);
        pro.waitFor();

        // 16个靶点都启动监控线程，对接完成后自动执行Python后处理
        monitorAndExecutePython(protein, workDir);
    }

    /**
     * 监控对接进程，完成后执行Python处理
     */
    private void monitorAndExecutePython(String protein, String workDir) {
        new Thread(() -> {
            try {
                System.out.println("[" + new java.util.Date() + "] Start monitoring " + protein + " 的对接进程");

                // 首次等待时间拉长到6秒，给新窗口/vina进程足够的启动时间，
                // 避免"进程还没真正起来就被误判为已经结束"
                Thread.sleep(6000);

                String processName = getProcessName(protein);
                while (isProcessRunning(processName)) {
                    Thread.sleep(5000);
                }

                System.out.println("[" + new java.util.Date() + "] " + protein + " 对接完成，开始执行Python处理");

                executePythonPostProcessing(protein, workDir);

            } catch (Exception e) {
                System.err.println("[" + new java.util.Date() + "] 监控或执行Python异常: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 检查进程是否正在运行
     */
    private boolean isProcessRunning(String processName) {
        try {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c tasklist |find \"" + processName + "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String str = reader.readLine();
            reader.close();
            return str != null;
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * 执行Python后处理脚本
     */
    private void executePythonPostProcessing(String protein, String workDir) {
        try {
            System.out.println("[" + new java.util.Date() + "] Start running the Python post-processing: " + protein);

            ProcessBuilder pb = new ProcessBuilder(
                    PYTHON_INTERPRETER,
                    PYTHON_SCRIPT,
                    workDir,
                    protein
            );

            pb.directory(new File(workDir));
            pb.redirectErrorStream(true);

            Process pythonProcess = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(pythonProcess.getInputStream(), "GBK")
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python output][" + protein + "] " + line);
            }

            int pythonExitCode = pythonProcess.waitFor();

            if (pythonExitCode == 0) {
                System.out.println("[" + new java.util.Date() + "] Python post-processing has been successfully completed: " + protein);
            } else {
                System.err.println("[" + new java.util.Date() + "] Python post-processing failed, exit code: " + pythonExitCode);
            }

            reader.close();

        } catch (Exception e) {
            System.err.println("[" + new java.util.Date() + "] Exception during execution of Python script: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getWorkDirectory(String protein) {
        return BASE_DIR + protein;
    }

    private String getDockingCommand(String protein) {
        return "dock.bat";
    }

    private String getProcessName(String protein) {
        return "vina" + protein + ".exe";
    }

    private boolean isProteinAvailable(String protein) {
        try {
            String processName = getProcessName(protein);
            Process pro = Runtime.getRuntime().exec("cmd.exe /c tasklist |find \"" + processName + "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String str = reader.readLine();
            reader.close();
            return str == null;
        } catch (IOException ex) {
            return false;
        }
    }
}