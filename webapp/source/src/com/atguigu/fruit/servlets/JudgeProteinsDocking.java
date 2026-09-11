package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/batch-status")
public class JudgeProteinsDocking extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 检查各个蛋白质的状态
        Map<String, String> proteinStatus = new HashMap<>();
        proteinStatus.put("protein1", getProteinStatus("protein1"));
        proteinStatus.put("protein2", getProteinStatus("protein2"));
        proteinStatus.put("protein3", getProteinStatus("protein3"));

        showStatusPage(response, proteinStatus);
    }

    private String getProteinStatus(String protein) {
        try {
            String processName = getProcessName(protein);
            Process pro = Runtime.getRuntime().exec("cmd.exe /c tasklist |find \"" + processName + "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String str = reader.readLine();
            reader.close();

            if (str != null) {
                return "运行中";
            } else {
                // 检查是否有结果文件
                if (hasResultFile(protein)) {
                    return "已完成";
                } else {
                    return "空闲";
                }
            }
        } catch (IOException ex) {
            return "状态未知";
        }
    }

    private boolean hasResultFile(String protein) {
        String resultPath = getWorkDirectory(protein) + "\\testligand3\\testligand3.pdb";
        return new java.io.File(resultPath).exists();
    }

    private String getWorkDirectory(String protein) {
        switch (protein) {
            case "protein1": return "D:\\AutodockVina";
            case "protein2": return "D:\\AutodockVina\\Protein2";
            case "protein3": return "D:\\AutodockVina\\Protein3";
            default: return "D:\\AutodockVina\\Default";
        }
    }

    private String getProcessName(String protein) {
        switch (protein) {
            case "protein1": return "vina123.exe";
            case "protein2": return "protein2_vina.exe";
            case "protein3": return "protein3_vina.exe";
            default: return "vina123.exe";
        }
    }

    private void showStatusPage(HttpServletResponse response, Map<String, String> proteinStatus)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 生成状态页面的HTML...
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<title>批量状态监控</title>");
        // 添加样式和自动刷新功能
        out.println("<meta http-equiv=\"refresh\" content=\"10\">"); // 10秒自动刷新
        out.println("</head>");
        out.println("<body>");

        // 显示各个蛋白质的状态
        for (Map.Entry<String, String> entry : proteinStatus.entrySet()) {
            String protein = entry.getKey();
            String status = entry.getValue();
            String displayName = getProteinDisplayName(protein);
            String statusColor = getStatusColor(status);

            out.println("<div style=\"margin: 20px; padding: 15px; border-left: 4px solid " + statusColor + ";\">");
            out.println("<h4>" + displayName + "</h4>");
            out.println("<span style=\"color: " + statusColor + "; font-weight: bold;\">" + status + "</span>");
            out.println("</div>");
        }

        out.println("</body>");
        out.println("</html>");
    }

    private String getProteinDisplayName(String protein) {
        switch (protein) {
            case "protein1": return "靶蛋白1 (6LU7)";
            case "protein2": return "靶蛋白2 (7BV2)";
            case "protein3": return "靶蛋白3 (6M0J)";
            default: return protein;
        }
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "运行中": return "#ffc107";
            case "已完成": return "#28a745";
            case "空闲": return "#6c757d";
            default: return "#dc3545";
        }
    }
}