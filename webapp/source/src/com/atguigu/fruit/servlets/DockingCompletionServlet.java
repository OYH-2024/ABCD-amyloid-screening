package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 查询靶点是否已经有可下载的对接结果。
 * 判断依据：该靶点文件夹下是否存在非空的 DockingResults.txt
 * （这个文件是 dock.bat 跑完对接后自动生成的，存在即代表对接已完成过，
 *  这个判断不依赖服务器内存状态，重启服务器/换浏览器都不受影响）
 *
 * 用法: GET /docking-completion?proteins=1AML,1BA4,7SP1
 * 返回: {"1AML":true,"1BA4":false,"7SP1":true}
 */
@WebServlet("/docking-completion")
public class DockingCompletionServlet extends HttpServlet {

    private static final String BASE_DIR = "E:/DataBase/WebSrever/Docking/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String proteinsParam = request.getParameter("proteins");
        PrintWriter out = response.getWriter();

        if (proteinsParam == null || proteinsParam.trim().isEmpty()) {
            out.write("{}");
            return;
        }

        String[] proteins = proteinsParam.split(",");
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (String protein : proteins) {
            protein = protein.trim();
            if (protein.isEmpty()) {
                continue;
            }

            File resultsFile = new File(BASE_DIR + protein, "DockingResults.txt");
            boolean completed = resultsFile.exists() && resultsFile.isFile() && resultsFile.length() > 0;

            if (!first) {
                json.append(",");
            }
            json.append("\"").append(protein).append("\":").append(completed);
            first = false;
        }
        json.append("}");

        out.write(json.toString());
    }
}
