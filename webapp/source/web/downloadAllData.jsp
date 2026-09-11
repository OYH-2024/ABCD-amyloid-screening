<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="com.atguigu.fruit.servlets.DownloadZip"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Download</title>
</head>
<body>
<%
    // 要打包的源目录：DATA文件夹下的全部内容（子文件夹和散落文件都会被打包进去）
    String sourcePath = "E:/DataBase/WebSrever/DATA";

    File sourceDir = new File(sourcePath);
    if (!sourceDir.exists() || !sourceDir.isDirectory()) {
%>
        <p>Data directory not found on the server.</p>
<%
        return;
    }

    DownloadZip downloadZip = new DownloadZip();
    // 复用已有的 createZip(sourcePath, zipFilePath, zipfileName) 方法，
    // 这个方法本来就是"把sourcePath整个目录递归打包"，正好符合"下载DATA目录全部文件"的需求
    File zipfile = downloadZip.createZip(sourcePath, "C:/Sources", "ABCDDB_Data");

    String path = zipfile.getPath();
    String fileName = path.substring(path.lastIndexOf(File.separator) + 1);

    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/x-download");
    response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));

    InputStream reader = null;
    OutputStream outp = null;
    byte[] bytes = new byte[1024];
    int len = 0;
    try {
        reader = new FileInputStream(path);
        outp = response.getOutputStream();
        while ((len = reader.read(bytes)) > 0) {
            outp.write(bytes, 0, len);
            outp.flush();
        }
        out.clear();
        out = pageContext.pushBody();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        if (reader != null) {
            reader.close();
        }
        downloadZip.delFile(zipfile);
    }
%>
</body>
</html>
