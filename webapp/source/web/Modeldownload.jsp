<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.OutputStream"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Map"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Download Model Prediction Results</title>
</head>
<body>
<%
    // Maps the "type" query parameter to the actual result file produced by each
    // disease's prediction script (see AD.py / TAU.py / Huntingtin.py / TUHe.py).
    Map<String, String> typeToFileName = new HashMap<>();
    typeToFileName.put("Ab", "AD_result.csv");
    typeToFileName.put("TuHe", "AlphaSynuclein_result.csv");
    typeToFileName.put("hunting", "Huntingtin_result.csv");
    typeToFileName.put("TAU", "TAU_result.csv");

    String outputDir = "E:\\DataBase\\WebSrever\\Model\\output\\";
    String type = request.getParameter("type");
    String fileName = (type != null) ? typeToFileName.get(type) : null;

    if (fileName == null) {
%>
<div style="text-align: center; margin-top: 50px;">
    <h2>Unknown or missing "type" parameter</h2>
    <p>Valid values are: Ab, TuHe, hunting, TAU</p>
</div>
<%
        return;
    }

    File file = new File(outputDir + fileName);

    if (file.exists() && file.isFile()) {
        response.setContentType("application/octet-stream");

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
    <p>Please finish the prediction for this model first, then come back to download.</p>
    <p style="color:#888;">Expected path: <%= outputDir + fileName %></p>
</div>
<%
    }
%>
</body>
</html>
