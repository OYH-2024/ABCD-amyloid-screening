<%--<%@ page language="java" contentType="text/html; charset=GBK"--%>
<%--         pageEncoding="GBK"%>--%>
<%--<%@ page import="com.atguigu.fruit.servlets.DownloadZip"%>--%>
<%--<%@ page import="java.io.File"%>--%>
<%--<%@ page import="java.io.FileInputStream"%>--%>
<%--<%@ page import="java.io.InputStream" %>--%>
<%--<%@ page import="java.io.OutputStream" %>--%>
<%--<%@ page import="java.net.URLEncoder" %>--%>
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">--%>
<%--<html>--%>
<%--<head>--%>
<%--    <meta http-equiv="Content-Type" content="text/html; charset=GBK">--%>
<%--    <title>Insert title here</title>--%>
<%--</head>--%>
<%--<body>--%>
<%--<%--%>
<%--    // 创建压缩包并返回压缩包位置--%>
<%--    DownloadZip downloadZip = new DownloadZip();--%>
<%--    //源路径：File zipfile = downloadZip.createZip("D:/AutodockVina/AutodockVina","D:/Sources","Sources");--%>
<%--    File zipfile = downloadZip.createZip("D:/AutodockVina/RES1","C:/Sources","Sources");--%>
<%--    String path=zipfile.getPath();--%>
<%--    // 获取文件名--%>
<%--    String fileName=path.substring(path.lastIndexOf("\\")+1);--%>
<%--    System.out.println(fileName);--%>
<%--    //制定浏览器头--%>
<%--    //如果图片名称是中文需要设置转码--%>
<%--    response.setCharacterEncoding("GBK");--%>
<%--    response.setContentType("application/x-download");//设置为下载application/x-download--%>
<%--    response.setHeader("content-disposition", "attachment;fileName="+URLEncoder.encode(fileName, "GBK"));--%>
<%--    InputStream reader = null;--%>
<%--    OutputStream outp = null;--%>
<%--    byte[] bytes = new byte[1024];--%>
<%--    int len = 0;--%>
<%--    try {--%>
<%--        // 读取文件--%>
<%--        reader = new FileInputStream(path);--%>
<%--        // 写入浏览器的输出流--%>
<%--        outp = response.getOutputStream();--%>
<%--        while ((len = reader.read(bytes)) > 0) {--%>
<%--            outp.write(bytes, 0, len);--%>
<%--            outp.flush();--%>

<%--        }--%>
<%--        out.clear();--%>
<%--        out = pageContext.pushBody();--%>
<%--    } catch (Exception e) {--%>
<%--        e.printStackTrace();--%>
<%--    } finally {--%>
<%--        if (reader != null) {--%>
<%--            reader.close();--%>
<%--        }--%>
<%--        //这里貌似不能关闭，如果关闭在同一个页面多次点击下载，会报错--%>
<%--        //if (outp != null)--%>
<%--        //   outp.close();--%>
<%--        downloadZip.delFile(zipfile);--%>
<%--    }--%>
<%--%>--%>
<%--</body>--%>
<%--</html>--%>

<%------------------------------------旧的代码在上，新的代码在下--%>


<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ page import="com.atguigu.fruit.servlets.DownloadZip"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Download</title>
</head>
<body>
<%
    // 基础目录，跟 FileUploadServlet.java / CmdProteinsDocking.java 保持一致
    String BASE_DIR = "E:/DataBase/WebSrever/Docking/";

    // 16个合法的靶点PDB ID，防止pdbID参数被篡改导致路径穿越或访问到不存在的目录
    List<String> VALID_IDS = Arrays.asList(
            "1AML", "1BA4", "1IYT", "1XQ8", "1Z0Q", "2BEG", "2M9R", "2NAO",
            "5OQV", "6A6B", "6OC9", "6RHY", "6SHS", "6TI7", "6YEJ", "7SP1"
    );

    String pdbID = request.getParameter("pdbID");

    if (pdbID == null || !VALID_IDS.contains(pdbID.toUpperCase())) {
%>
<p>Invalid or missing pdbID parameter. Please go back and click a valid download button.</p>
<%
        return;
    }

    // 统一转成大写，跟文件夹命名保持一致
    pdbID = pdbID.toUpperCase();

    // 该靶点的根目录，例如 E:/DataBase/WebSrever/Docking/1AML
    String sourcePath = BASE_DIR + pdbID;

    DownloadZip downloadZip = new DownloadZip();
    // 只打包 sourcePath 下面的全部子文件夹（配体对接结果），不打包 dock.bat / vina程序等散落文件
    File zipfile = downloadZip.createZipFromSubfolders(sourcePath, "C:/Sources", pdbID);

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
        // 注意：这里outp不能关闭，重复关闭同一页面响应流会报错
        downloadZip.delFile(zipfile);
    }
%>
</body>
</html>

