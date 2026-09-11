//package com.atguigu.fruit.servlets;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@WebServlet("/ExecutePythonServlet")
//public class ExecutePythonServlet extends HttpServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
//            // 创建一个新线程来执行Python，防止阻塞
//            new Thread(() -> {
//                try {
//                    ProcessBuilder processBuilder = new ProcessBuilder();
//                    processBuilder.command("C:\\Users\\Lenovo\\PyCharmProjects\\pythonProject\\venv\\Scripts\\python.exe",
//                            "E:\\DataBase\\PythonCode\\FileProcess.py");
//
//                    // 启动进程
//                    Process process = processBuilder.start();
//
//                    // 可选：记录执行结果
//                    int exitCode = process.waitFor();
//                    System.out.println("Python执行完毕，退出码: " + exitCode);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//
//            // 立即返回响应
//            response.setStatus(HttpServletResponse.SC_OK);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    // 支持GET请求，以便navigator.sendBeacon可以工作
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        doPost(request, response);
//    }
//}