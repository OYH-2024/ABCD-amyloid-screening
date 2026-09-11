import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/pdbfile.do")
public class PdbFileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filename = request.getParameter("filename");

        // 1. 参数校验
        if (filename == null || filename.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing filename parameter");
            return;
        }

        // 2. 防路径穿越攻击：拒绝包含 .. 或 / 或 \ 的文件名
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid filename");
            return;
        }

        // 3. 动态定位到部署目录下的 1amlpdb 文件夹，不再硬编码绝对路径
        //    本地是 E:\...\war_exploded\1amlpdb
        //    服务器上是 服务器实际部署路径\1amlpdb
        //    getRealPath 会自动适配当前实际部署位置，换服务器/换电脑都不用改代码
        String basePath = getServletContext().getRealPath("/1amlpdb/");
        File file = new File(basePath, filename);

        // 4. 文件存在性检查，避免走到FileInputStream才报错
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + filename);
            return;
        }

        // 5. 确认文件确实存在后，才设置响应头，避免"声明了下载但没内容"的畸形响应
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLengthLong(file.length());

        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }
}