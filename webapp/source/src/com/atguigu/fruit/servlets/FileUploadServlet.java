package com.atguigu.fruit.servlets;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/fileUpload")
public class FileUploadServlet extends HttpServlet {

    // 基础目录 + 16个靶点子文件夹
    private static final String BASE_DIR = "E:/DataBase/WebSrever/Docking/";
    private static final String[] TARGET_FOLDERS = {
            "1AML", "1BA4", "1IYT", "1XQ8", "1Z0Q", "2BEG", "2M9R", "2NAO",
            "5OQV", "6A6B", "6OC9", "6RHY", "6SHS", "6TI7", "6YEJ", "7SP1"
    };

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 创建16个目标目录（如果不存在）
        createDirectories();

        // 配置文件上传工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置内存阈值 - 超过此值将写入临时文件
        factory.setSizeThreshold(1024 * 1024); // 1MB
        // 设置临时目录
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        factory.setRepository(tempDir);

        ServletFileUpload fileUpload = new ServletFileUpload(factory);
        // 设置最大文件大小
        fileUpload.setFileSizeMax(10 * 1024 * 1024); // 10MB
        fileUpload.setSizeMax(50 * 1024 * 1024); // 50MB总大小

        List<String> uploadedFiles = new ArrayList<>();

        try {
            List<FileItem> list = fileUpload.parseRequest(req);

            // 首先验证所有文件格式
            for (FileItem item : list) {
                if (!item.isFormField() && item.getSize() > 0) {
                    String fileName = item.getName();
                    if (fileName != null && !fileName.toLowerCase().endsWith(".pdbqt")&&
                            !fileName.toLowerCase().endsWith(".smi") && !fileName.toLowerCase().endsWith(".csv") ) {
                        generateErrorPage(resp, fileName);
                        return;
                    }
                }
            }

            // 验证通过后，先清空16个目标文件夹里的旧pdbqt文件
            clearPdbqtFiles();
            clearLigandResultFolders();

            // 保存文件到16个目标文件夹
            for (FileItem item : list) {
                if (!item.isFormField() && item.getSize() > 0) {
                    String fileName = item.getName();
                    if (fileName != null && !fileName.trim().isEmpty()) {

                        for (String folder : TARGET_FOLDERS) {
                            saveFileToDirectory(item, BASE_DIR + folder + "/", fileName);
                        }

                        uploadedFiles.add(fileName);
                    }
                }
            }

            // 清理临时文件
            for (FileItem item : list) {
                if (!item.isFormField()) {
                    item.delete(); // 删除临时文件
                }
            }

            if (uploadedFiles.size() > 0) {
                generateSuccessPage(resp, uploadedFiles);
            } else {
                generateErrorPage(resp, "没有找到有效的文件");
            }

        } catch (Exception e) {
            e.printStackTrace();
            generateSystemErrorPage(resp, e.getMessage());
        }
    }

    // 创建所需的16个目录
    private void createDirectories() {
        for (String folder : TARGET_FOLDERS) {
            createDirectory(BASE_DIR + folder + "/");
        }
    }

    private void createDirectory(String dirPath) {
        try {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Create a directory: " + dirPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to create directory: " + dirPath + " - " + e.getMessage());
        }
    }

    // 上传前清空16个目标文件夹里所有的pdbqt文件（排除靶蛋白受体文件）
    private void clearPdbqtFiles() {
        for (String folder : TARGET_FOLDERS) {
            File dir = new File(BASE_DIR + folder + "/");
            if (dir.exists() && dir.isDirectory()) {
                // 靶蛋白受体文件名 = 文件夹名 + .pdbqt，例如 1AML/1AML.pdbqt
                String proteinFileName = folder + ".pdbqt";

                File[] files = dir.listFiles((d, name) ->
                        name.toLowerCase().endsWith(".pdbqt")
                                && !name.equalsIgnoreCase(proteinFileName)
                );

                if (files != null) {
                    for (File f : files) {
                        if (f.delete()) {
                            System.out.println("已删除旧文件: " + f.getAbsolutePath());
                        } else {
                            System.err.println("删除失败: " + f.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    /**
     * 上传前清空16个目标文件夹里所有的配体对接结果子文件夹
     * （比如 testligand2/、mol2/ 这些，是上一轮 dock.bat 跑出来的结果，
     *  不清理的话会跟这次新配体的结果混在一起，下载打包时会把新旧结果一起打包，容易搞混）
     */
    private void clearLigandResultFolders() {
        for (String folder : TARGET_FOLDERS) {
            File dir = new File(BASE_DIR + folder + "/");
            if (dir.exists() && dir.isDirectory()) {
                File[] subDirs = dir.listFiles(File::isDirectory);
                if (subDirs != null) {
                    for (File subDir : subDirs) {
                        boolean deleted = deleteDirectoryRecursively(subDir);
                        if (deleted) {
                            System.out.println("已删除旧的配体结果文件夹: " + subDir.getAbsolutePath());
                        } else {
                            System.err.println("删除配体结果文件夹失败: " + subDir.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    /**
     * 递归删除一个文件夹及其内部所有文件/子文件夹
     */
    private boolean deleteDirectoryRecursively(File dir) {
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    deleteDirectoryRecursively(entry);
                } else {
                    entry.delete();
                }
            }
        }
        return dir.delete();
    }













    // 安全地保存文件到指定目录
    private void saveFileToDirectory(FileItem item, String dirPath, String fileName) {
        try {
            File targetFile = new File(dirPath + fileName);
            item.write(targetFile);
            System.out.println("文件保存成功: " + targetFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("保存文件失败: " + dirPath + fileName + " - " + e.getMessage());
        }
    }

    // 生成成功页面
    private void generateSuccessPage(HttpServletResponse resp, List<String> uploadedFiles) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>File uploaded successfully – Online Molecular Docking System</title>");
        out.println("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; margin: 0; }");
        out.println(".success-container { max-width: 600px; width: 90%; background: rgba(255,255,255,0.95); padding: 50px; border-radius: 8px; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }");
        out.println(".success-icon { color: #28a745; font-size: 4rem; margin-bottom: 25px; }");
        out.println(".success-title { color: #2a5298; font-size: 2.2rem; font-weight: 700; margin-bottom: 20px; }");
        out.println(".file-list { background: #f8f9fa; padding: 15px; border-radius: 6px; margin: 20px 0; text-align: left; }");
        out.println(".btn-primary { background: linear-gradient(135deg, #2a5298, #1e3c72); color: white; padding: 15px 35px; border: none; border-radius: 8px; text-decoration: none; margin: 10px; display: inline-flex; align-items: center; gap: 10px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='success-container'>");
        out.println("<div class='success-icon'><i class='fas fa-check-circle'></i></div>");
        out.println("<h2 class='success-title'>The file has been uploaded successfully!</h2>");

        out.println("<div class='file-list'>");
        out.println("<h5>Files successfully uploaded (" + uploadedFiles.size() + " 个):</h5>");
        for (String fileName : uploadedFiles) {
            out.println("<div><i class='fas fa-file-alt'></i> " + fileName + "</div>");
        }
        out.println("</div>");

        out.println("<p>The files were submitted successfully.<br>");
        out.println("Please return to the molecular docking interface for operation.</p>");

        out.println("<a href='cmd.html' class='btn-primary'>");
        out.println("<i class='fas fa-arrow-left'></i> Back to the docking interface</a>");

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    // 生成错误页面
    private void generateErrorPage(HttpServletResponse resp, String fileName) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Invalid file format</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; margin: 0; }");
        out.println(".error-container { max-width: 600px; background: rgba(255,255,255,0.95); padding: 50px; border-radius: 8px; text-align: center; }");
        out.println(".error-icon { color: #dc3545; font-size: 4rem; margin-bottom: 25px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='error-container'>");
        out.println("<div class='error-icon'><i class='fas fa-exclamation-triangle'></i></div>");
        out.println("<h2 style='color: #dc3545;'>Invalid file format</h2>");
        out.println("<p>Please check the file format!</p>");
        out.println("<p><strong>The files you have uploaded：</strong> " + fileName + "</p>");
        out.println("<a href='javascript:history.back()' style='background: #2a5298; color: white; padding: 15px 30px; text-decoration: none; border-radius: 6px;'>Re-upload</a>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    // 生成系统错误页面
    private void generateSystemErrorPage(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>System error</title>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; margin: 0; }");
        out.println(".error-container { max-width: 500px; background: rgba(255,255,255,0.95); padding: 40px; border-radius: 8px; text-align: center; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class='error-container'>");
        out.println("<h2 style='color: #dc3545;'>系统错误</h2>");
        out.println("<p>An error occurred during the upload. Please try again.</p>");
        out.println("<details><summary>Error details</summary><p>" + errorMessage + "</p></details>");
        out.println("<a href='add.jsp' style='color: #2a5298;'>Return and retry</a>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}