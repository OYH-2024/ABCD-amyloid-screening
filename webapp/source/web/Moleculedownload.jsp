<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.OutputStream"%>
<%@ page import="java.net.URLEncoder"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Download Molecular Descriptor Result File</title>
</head>
<body>
<%
    // Path to the molecular descriptor calculation result file (output of testrd.py)
    String filePath = "E:\\DataBase\\WebSrever\\MoleculeDescription\\output\\RES.csv";
    File file = new File(filePath);

    if (file.exists() && file.isFile()) {
        response.setContentType("application/octet-stream");

        String fileName = "RES.csv";
        String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        response.setContentLength((int) file.length());

        FileInputStream fileIn = null;
        try {
            fileIn = new FileInputStream(file);
            OutputStream outStream = response.getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }

            out.clear();
            out = pageContext.pushBody();
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
    } else {
%>
<div style="text-align: center; margin-top: 50px;">
    <h2>Result file not found</h2>
    <p>Please finish the molecular descriptor calculation first, then come back to download.</p>
    <p style="color:#888;">Expected path: <%= filePath %></p>
</div>
<%
    }
%>
</body>
</html>
