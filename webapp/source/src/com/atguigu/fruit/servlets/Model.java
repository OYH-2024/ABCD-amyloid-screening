package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/hello2")
public class Model extends HttpServlet {

    // Directory where the prediction scripts live
    private static final String CODE_DIR = "E:\\DataBase\\WebSrever\\Model\\code\\";

    // Directory where ModelAddCsvServlet saves each disease's dedicated CSV
    private static final String INPUT_DIR = "E:\\DataBase\\WebSrever\\Model\\input\\";

    // Status is persisted to a small file per disease type, NOT kept in memory --
    // an in-memory Map would be reset every time Tomcat hot-reloads the app (a new
    // classloader means a brand new static field), which caused the background
    // thread's result to become invisible to later requests. Writing to disk survives
    // redeploys, since the running Python process keeps writing to the same file
    // regardless of which classloader is currently serving web requests.
    private static final String STATUS_DIR = "E:\\DataBase\\WebSrever\\Model\\status\\";

    private static class DiseaseConfig {
        final String displayName;
        final String scriptFileName;
        final String inputCsvFileName;
        final String[] requiredProteinIds;

        DiseaseConfig(String displayName, String scriptFileName, String inputCsvFileName, String[] requiredProteinIds) {
            this.displayName = displayName;
            this.scriptFileName = scriptFileName;
            this.inputCsvFileName = inputCsvFileName;
            this.requiredProteinIds = requiredProteinIds;
        }
    }

    private static final Map<String, DiseaseConfig> DISEASE_CONFIGS = new LinkedHashMap<>();
    static {
        DISEASE_CONFIGS.put("ad", new DiseaseConfig(
                "Abeta", "AD.py", "ADModelSource.csv",
                new String[]{"1Z0Q", "6RHY", "6TI7", "1AML", "1BA4", "1IYT",
                        "2BEG", "2M9R", "2NAO", "5OQV", "6OC9", "6SHS"}
        ));
        DISEASE_CONFIGS.put("tau", new DiseaseConfig(
                "Tau", "TAU.py", "TAUModelSource.csv",
                new String[]{"7SP1"}
        ));
        DISEASE_CONFIGS.put("huntingtin", new DiseaseConfig(
                "Huntingtin", "Huntingtin.py", "HuntingtinModelSource.csv",
                new String[]{"6YEJ"}
        ));
        DISEASE_CONFIGS.put("TUHe", new DiseaseConfig(
                "Alpha-Synuclein", "TUHe.py", "TUHeModelSource.csv",
                new String[]{"1XQ8", "6A6B"}
        ));
    }

    // ---- Shared status file helpers (also used by ModelStatusServlet) ----

    static synchronized void writeStatus(String modelType, String status) {
        try {
            File dir = new File(STATUS_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File statusFile = new File(dir, modelType + ".status");
            try (Writer w = new OutputStreamWriter(new FileOutputStream(statusFile), StandardCharsets.UTF_8)) {
                w.write(status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String readStatus(String modelType) {
        File statusFile = new File(STATUS_DIR, modelType + ".status");
        if (!statusFile.exists()) {
            return "unknown";
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(statusFile), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            return line == null ? "unknown" : line.trim();
        } catch (IOException e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String modelType = request.getParameter("type");
        String mode = request.getParameter("mode"); // null on the first click, "result" when polled back

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><meta charset='UTF-8'><title>Modeling</title>");
        out.println(commonStyle());
        out.println("</head>");
        out.println("<body><div class='container'>");

        DiseaseConfig config = DISEASE_CONFIGS.get(modelType);
        if (config == null) {
            out.println("<h2>Unknown model type</h2>");
        } else if ("result".equals(mode)) {
            printResultPage(out, modelType, config);
        } else {
            handlePrediction(out, modelType, config);
        }

        out.println("</div></body></html>");
    }

    private void handlePrediction(PrintWriter out, String modelType, DiseaseConfig config) throws IOException {
        File csvFile = new File(INPUT_DIR + config.inputCsvFileName);

        if (!csvFile.exists()) {
            out.println("<div class='icon error'>&#10007;</div>");
            out.println("<h2>No uploaded data found for the " + config.displayName + " model</h2>");
            out.println("<p>Please upload a CSV file with binding energy values for all required target proteins first, then come back to start the prediction.</p>");
            out.println("<a href='model.html' class='button'>Back to previous page</a>");
            return;
        }

        List<String> missingProteinIds = checkMissingBindingEnergy(csvFile, config.requiredProteinIds);

        if (!missingProteinIds.isEmpty()) {
            out.println("<div class='icon error'>&#10007;</div>");
            out.println("<h2>Missing binding energy data</h2>");
            out.println("<p>The uploaded file is missing a binding energy value for the following target protein(s). Please go back, fill them in, and re-upload.</p>");
            out.println("<div class='file-details'>");
            out.println("<ul style='margin:0;padding-left:20px;'>");
            for (String proteinId : missingProteinIds) {
                out.println("<li>" + proteinId + "</li>");
            }
            out.println("</ul>");
            out.println("</div>");
            out.println("<a href='model.html' class='button'>Back to previous page</a>");
            return;
        }

        // Everything checks out -- kick off the (slow) Python script on a background
        // thread and immediately return the "running" page, which polls and then
        // navigates to the actual result page once it's done.
        writeStatus(modelType, "running");

        Thread worker = new Thread(() -> {
            int exitCode = runModel(CODE_DIR + config.scriptFileName);
            writeStatus(modelType, exitCode == 0 ? "success" : ("error:" + exitCode));
        });
        worker.setDaemon(true);
        worker.start();

        printRunningPage(out, modelType, config.displayName);
    }

    // Same card style as the final success/error page -- spinner instead of the icon.
    private void printRunningPage(PrintWriter out, String modelType, String displayName) {
        out.println("<div class='spinner'></div>");
        out.println("<h2>Running the " + displayName + " protein prediction model...</h2>");
        out.println("<p>This can take a little while the first time (training a fresh model). Please keep this page open.</p>");

        out.println("<script>");
        out.println("  const poll = setInterval(function() {");
        out.println("    fetch('modelStatus?type=" + modelType + "')");
        out.println("      .then(r => r.text())");
        out.println("      .then(function(status) {");
        out.println("        if (status !== 'running') {");
        out.println("          clearInterval(poll);");
        out.println("          window.location.href = 'hello2?type=" + modelType + "&mode=result';");
        out.println("        }");
        out.println("      });");
        out.println("  }, 2000);");
        out.println("</script>");
    }

    // Final state page -- exact same layout/style as the running page, just with the
    // icon (check or cross) instead of the spinner. Reads the already-finished status
    // from the status file; does not touch Python or the CSV again.
    private void printResultPage(PrintWriter out, String modelType, DiseaseConfig config) {
        String status = readStatus(modelType);

        if ("success".equals(status)) {
            out.println("<div class='icon success'>&#10003;</div>");
            out.println("<h2>The " + config.displayName + " protein prediction model has successfully completed!</h2>");
            out.println("<p>Please check the results on the download page.</p>");
            out.println("<a href='model.html' class='button'>Back to previous page</a>");
        } else if (status.startsWith("error:")) {
            String exitCode = status.substring("error:".length());
            out.println("<div class='icon error'>&#10007;</div>");
            out.println("<h2>The " + config.displayName + " protein prediction model failed to execute (Exit code: " + exitCode + ")</h2>");
            out.println("<p>Please check the server console log for details, then try again.</p>");
            out.println("<a href='model.html' class='button'>Back to previous page</a>");
        } else {
            out.println("<div class='icon error'>&#10007;</div>");
            out.println("<h2>No prediction is currently running for the " + config.displayName + " model</h2>");
            out.println("<p>Please start the prediction from the model page.</p>");
            out.println("<a href='model.html' class='button'>Back to previous page</a>");
        }
    }

    // Reads the header + first data row of the uploaded CSV, and returns the list of
    // required protein IDs whose column is either missing entirely or has an empty value.
    private List<String> checkMissingBindingEnergy(File csvFile, String[] requiredProteinIds) throws IOException {
        List<String> missing = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(csvFile), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                for (String proteinId : requiredProteinIds) {
                    missing.add(proteinId);
                }
                return missing;
            }
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }
            String[] headers = headerLine.split(",", -1);

            String dataLine = reader.readLine();
            String[] values = (dataLine != null) ? dataLine.split(",", -1) : new String[0];

            for (String proteinId : requiredProteinIds) {
                int colIndex = -1;
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].trim().equalsIgnoreCase(proteinId)) {
                        colIndex = i;
                        break;
                    }
                }

                if (colIndex == -1) {
                    missing.add(proteinId);
                } else if (colIndex >= values.length || values[colIndex].trim().isEmpty()) {
                    missing.add(proteinId);
                }
            }
        }

        return missing;
    }

    private int runModel(String scriptPath) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("C:\\Users\\Lenovo\\PycharmProjects\\pythonProject\\venv\\Scripts\\python.exe", scriptPath);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
            return exitCode;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String commonStyle() {
        return "<style>"
                + "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); margin: 0; padding: 0; min-height: 100vh; display: flex; justify-content: center; align-items: center; }"
                + ".container { background-color: white; border-radius: 10px; box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1); width: 85%; max-width: 650px; padding: 30px; text-align: center; margin: 30px 0; }"
                + ".icon { font-size: 70px; margin-bottom: 15px; }"
                + ".success { color: #28a745; }"
                + ".error { color: #dc3545; }"
                + "h2 { color: #333; margin-bottom: 15px; }"
                + "p { color: #666; margin-bottom: 10px; word-break: break-word; }"
                + ".file-details { background-color: #f9f9f9; border-radius: 5px; padding: 15px; margin: 20px 0; text-align: left; }"
                + ".button { background: linear-gradient(135deg, #2a5298, #1e3c72); color: white; border: none; padding: 12px 24px; text-decoration: none; display: inline-block; font-size: 16px; margin: 15px 0 0; cursor: pointer; border-radius: 4px; }"
                + ".spinner { width: 60px; height: 60px; border: 6px solid #e0e0e0; border-top-color: #2a5298; border-radius: 50%; margin: 0 auto 20px; animation: spin 1s linear infinite; }"
                + "@keyframes spin { to { transform: rotate(360deg); } }"
                + "</style>";
    }
}