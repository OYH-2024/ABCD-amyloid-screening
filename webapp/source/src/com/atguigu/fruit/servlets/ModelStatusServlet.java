package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Lightweight polling endpoint used by the "running" page rendered by Model.java
 * (hello2). Returns the current status of a disease's prediction job as plain text,
 * read from the status file on disk (see Model.readStatus) -- NOT from an in-memory
 * map, so it survives Tomcat hot-reloads happening mid-run:
 *   "running"        -- still in progress
 *   "success"        -- finished, exit code 0
 *   "error:<code>"   -- finished, non-zero exit code
 *   "unknown"        -- nothing has been started for this type yet
 */
@WebServlet("/modelStatus")
public class ModelStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");

        resp.setContentType("text/plain;charset=UTF-8");
        // Make sure the browser never caches a stale status while polling
        resp.setHeader("Cache-Control", "no-store");

        PrintWriter out = resp.getWriter();
        String status = (type == null) ? "unknown" : Model.readStatus(type);
        out.print(status);
    }
}