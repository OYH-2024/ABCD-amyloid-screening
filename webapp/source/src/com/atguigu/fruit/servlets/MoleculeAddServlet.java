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
import java.util.List;


@WebServlet("/AddMoleculeFileUpload")
public class MoleculeAddServlet extends HttpServlet {

    // Dedicated input/output directories for the molecular descriptor calculation feature
    private static final String INPUT_DIR = "E:/DataBase/WebSrever/MoleculeDescription/input/";
    private static final String OUTPUT_DIR = "E:/DataBase/WebSrever/MoleculeDescription/output/";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // Make sure both directories exist
        File inputDir = new File(INPUT_DIR);
        File outputDir = new File(OUTPUT_DIR);
        if (!inputDir.exists()) {
            inputDir.mkdirs();
        }
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        /* Get the file upload factory object */
        FileItemFactory fileItemFactory = new DiskFileItemFactory();
        /* Get the file upload component */
        ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);

        try {
            /* Parse the request.
               Every submitted field in the form is a FileItem. */
            List<FileItem> list = fileUpload.parseRequest(req);

            // Step 1: validate the extension first. Reject and stop immediately if anything is invalid.
            // Nothing is deleted or written until validation passes.
            for (FileItem item : list) {
                if (!item.isFormField() && item.getSize() > 0) {
                    String fileName = item.getName();
                    if (fileName == null || !fileName.toLowerCase().endsWith(".smi")) {
                        writeErrorPage(out, "Only .smi files are allowed. The file you uploaded was: " + fileName);
                        return;
                    }
                }
            }

            // Step 2: validation passed -- clear out ALL existing files in both input and output
            // directories before saving the new upload, so a fresh calculation always starts clean.
            clearAllFiles(inputDir);
            clearAllFiles(outputDir);

            // Step 3: save the new file, keeping the original uploaded filename
            String savedFileName = null;
            for (FileItem item : list) {
                if (!item.isFormField() && item.getSize() > 0) {
                    String fileName = item.getName();
                    if (fileName != null && !fileName.trim().isEmpty()) {
                        /* If it's a file, write it into the dedicated descriptor-calculation input directory */
                        item.write(new File(INPUT_DIR + fileName));
                        savedFileName = fileName;
                        System.out.println("SMI file saved: " + INPUT_DIR + fileName);
                    }
                }
            }

            if (savedFileName != null) {
                writeSuccessPage(out, savedFileName);
            } else {
                writeErrorPage(out, "No valid file was received, please choose a file and upload again");
            }

        } catch (Exception e) {
            e.printStackTrace();
            writeErrorPage(out, "A system error occurred during upload: " + e.getMessage());
        }
    }

    // Delete every file directly inside the given directory (does not recurse into subdirectories)
    private void clearAllFiles(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isFile()) {
                if (f.delete()) {
                    System.out.println("Cleared old file: " + f.getAbsolutePath());
                } else {
                    System.err.println("Failed to delete: " + f.getAbsolutePath());
                }
            }
        }
    }

    private void writeSuccessPage(PrintWriter out, String fileName) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Molecular Descriptor Calculation</title>");
        out.println("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; margin: 0; }");
        out.println(".result-container { max-width: 600px; width: 90%; background: rgba(255,255,255,0.95); padding: 50px; border-radius: 8px; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }");
        out.println(".result-icon { color: #28a745; font-size: 4rem; margin-bottom: 25px; }");
        out.println(".result-title { color: #2a5298; font-size: 2rem; font-weight: 700; margin-bottom: 20px; }");
        out.println(".file-box { background: #f8f9fa; padding: 15px; border-radius: 6px; margin: 20px 0; }");
        out.println(".btn-primary { background: linear-gradient(135deg, #2a5298, #1e3c72); color: white; padding: 15px 35px; border: none; border-radius: 50px; text-decoration: none; margin-top: 20px; display: inline-flex; align-items: center; gap: 10px; }");
        out.println(".btn-primary:hover { color: white; text-decoration: none; opacity: 0.9; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='result-container'>");
        out.println("<div class='result-icon'><i class='fas fa-check-circle'></i></div>");
        out.println("<h2 class='result-title'>The file was submitted successfully</h2>");
        out.println("<div class='file-box'><i class='fas fa-file-alt'></i> Saved file: " + fileName + "</div>");
        out.println("<p>Please return to the molecular descriptor calculation interface for operation......</p>");
        out.println("<a href='molcule.html' class='btn-primary'><i class='fas fa-arrow-left'></i> Back to the descriptor calculation interface</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

    private void writeErrorPage(PrintWriter out, String message) {
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Upload Failed</title>");
        out.println("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css'>");
        out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css'>");
        out.println("<style>");
        out.println("body { font-family: 'Segoe UI', sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; margin: 0; }");
        out.println(".result-container { max-width: 600px; width: 90%; background: rgba(255,255,255,0.95); padding: 50px; border-radius: 8px; text-align: center; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }");
        out.println(".result-icon { color: #dc3545; font-size: 4rem; margin-bottom: 25px; }");
        out.println(".result-title { color: #dc3545; font-size: 2rem; font-weight: 700; margin-bottom: 20px; }");
        out.println(".btn-retry { background: #2a5298; color: white; padding: 15px 30px; border: none; border-radius: 50px; text-decoration: none; margin-top: 10px; display: inline-block; }");
        out.println(".btn-retry:hover { color: white; text-decoration: none; opacity: 0.9; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='result-container'>");
        out.println("<div class='result-icon'><i class='fas fa-exclamation-triangle'></i></div>");
        out.println("<h2 class='result-title'>Upload Failed</h2>");
        out.println("<p>" + message + "</p>");
        out.println("<a href='javascript:history.back()' class='btn-retry'>Go back and try again</a>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }

}