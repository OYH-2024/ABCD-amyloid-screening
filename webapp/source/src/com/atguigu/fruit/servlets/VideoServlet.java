package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.OutputStream;

/**
 * 把服务器本地磁盘上的视频文件（不在web部署目录里）以流的方式提供给浏览器播放。
 * 支持HTTP Range请求（浏览器拖动视频进度条时会发这种请求），
 * 不支持的话，视频只能从头播到尾，没法拖拽跳转。
 *
 * 用法: GET /video
 */
@WebServlet("/video")
public class VideoServlet extends HttpServlet {

    // 视频文件的实际磁盘路径
    private static final String VIDEO_PATH = "E:/DataBase/WebSrever/MV/1.mp4";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        File videoFile = new File(VIDEO_PATH);

        if (!videoFile.exists() || !videoFile.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video file not found");
            return;
        }

        long fileLength = videoFile.length();
        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");

        String rangeHeader = request.getHeader("Range");

        long start = 0;
        long end = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            // 解析形如 "bytes=1000-" 或 "bytes=1000-2000" 的Range请求头
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                start = 0;
                end = fileLength - 1;
            }

            if (end > fileLength - 1) {
                end = fileLength - 1;
            }

            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
        } else {
            response.setStatus(HttpServletResponse.SC_OK); // 200
        }

        long contentLength = end - start + 1;
        response.setHeader("Content-Length", String.valueOf(contentLength));

        try (RandomAccessFile raf = new RandomAccessFile(videoFile, "r");
             OutputStream out = response.getOutputStream()) {

            raf.seek(start);

            byte[] buffer = new byte[8192];
            long remaining = contentLength;

            while (remaining > 0) {
                int readLen = raf.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (readLen == -1) {
                    break;
                }
                out.write(buffer, 0, readLen);
                remaining -= readLen;
            }
            out.flush();

        } catch (IOException e) {
            // 用户拖动进度条/关闭视频时，浏览器经常会中断连接，
            // 这里的IO异常大多是正常现象，打个日志就行，不用当成严重错误处理
            System.out.println("[VideoServlet] Client closed the connection early (normal when seeking): " + e.getMessage());
        }
    }
}
