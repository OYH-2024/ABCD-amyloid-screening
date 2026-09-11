package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/hello3")
public class Molcule extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("    <meta charset='UTF-8'>");
        out.println("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("    <title>Molecular Processing</title>");
        out.println("    <style>");
        out.println("        body {");
        out.println("            font-family: 'Segoe UI', sans-serif;");
        out.println("            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);");
        out.println("            margin: 0;");
        out.println("            padding: 0;");
        out.println("            min-height: 100vh;");
        out.println("            display: flex;");
        out.println("            justify-content: center;");
        out.println("            align-items: center;");
        out.println("        }");
        out.println("        .container {");
        out.println("            background-color: #ffffff;");
        out.println("            border-radius: 15px;");
        out.println("            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);");
        out.println("            padding: 40px;");
        out.println("            width: 80%;");
        out.println("            max-width: 800px;");
        out.println("            text-align: center;");
        out.println("        }");
        out.println("        h2 { color: #4a6572; margin-bottom: 20px; }");
        out.println("        .status { display: flex; align-items: center; justify-content: center; margin: 30px 0; }");
        out.println("        .status-icon { width: 50px; height: 50px; background-color: #4CAF50; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-right: 15px; }");
        out.println("        .status-icon:before { content: '\\2713'; color: white; font-size: 24px; }");
        out.println("        .status-text { color: #4CAF50; font-size: 18px; font-weight: 500; }");
        out.println("        .btn { background-color: #344955; color: white; padding: 12px 24px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; text-decoration: none; display: inline-block; margin-top: 20px; }");
        out.println("        .btn:hover { background-color: #232f34; }");
        out.println("        .info { background-color: #e3f2fd; border-left: 4px solid #2196F3; padding: 15px; margin: 30px 0; text-align: left; color: #555; }");
        out.println("    </style>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <div class='container'>");
        out.println("        <h2>Molecular Processing System</h2>");
        out.println("        <div class='status'>");
        out.println("            <div class='status-icon'></div>");
        out.println("            <div class='status-text'>Molecule computer is running successfully</div>");
        out.println("        </div>");
        out.println("        <div class='info'>");
        out.println("            <p>The molecular descriptor calculation task has been initiated. The system is currently executing the Python script.</p>");
        out.println("            <p>Please check the console output for detailed progress information, then go to the download page.</p>");
        out.println("        </div>");
        out.println("        <a href='molcule.html' class='btn'>Return to Calculate Page</a>");
        out.println("    </div>");
        out.println("</body>");
        out.println("</html>");

        Act();
    }

    public void Act() {
        ProcessBuilder processBuilder = new ProcessBuilder();

        // Path to the Python interpreter -- if this server's interpreter lives somewhere else, update it here
        String pythonExe = "C:\\Users\\Lenovo\\PyCharmProjects\\pythonProject\\venv\\Scripts\\python.exe";

        // Molecular descriptor calculation script, now stored under the code directory
        String scriptPath = "E:\\DataBase\\WebSrever\\MoleculeDescription\\code\\testrd.py";

        processBuilder.command(pythonExe, scriptPath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}