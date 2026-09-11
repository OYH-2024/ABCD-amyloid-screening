package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 展示批量启动失败原因的页面。纯GET接口，随便刷新都没有副作用。
 * 用法: GET /batch-error?message=xxx
 */
@WebServlet("/docking-batch-error")
public class BatchErrorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String message = request.getParameter("message");
        if (message == null || message.trim().isEmpty()) {
            message = "Unknown error";
        }

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<title>Start-up failure - Molecular docking system</title>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">");
        out.println("<style>");
        out.println("body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".error-container { max-width: 600px; background: rgba(255,255,255,0.95); padding: 40px; border-radius: 8px; text-align: center; }");
        out.println(".error-icon { font-size: 4rem; color: #dc3545; margin-bottom: 20px; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class=\"error-container\">");
        out.println("<div class=\"error-icon\"><i class=\"fas fa-exclamation-triangle\"></i></div>");
        out.println("<h2 style=\"color: #dc3545;\">Start-up failed</h2>");
        out.println("<p>" + message + "</p>");
        out.println("<a href=\"javascript:history.back()\" class=\"btn btn-primary\">Return and retry</a>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }
}
