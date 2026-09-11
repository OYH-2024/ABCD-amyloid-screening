package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 展示"批量启动之后各靶点运行状态"的页面。
 * 这是一个纯GET接口——只负责查询和展示，不会触发/重新触发任何对接，
 * 所以用户可以放心刷新这个页面任意多次，不会有副作用。
 *
 * 用法: GET /batch-status?proteins=1AML,6YEJ
 */
@WebServlet("/docking-batch-status")
public class BatchStatusServlet extends HttpServlet {

    private static final String[] TARGET_FOLDERS = {
            "1AML", "1BA4", "1IYT", "1XQ8", "1Z0Q", "2BEG", "2M9R", "2NAO",
            "5OQV", "6A6B", "6OC9", "6RHY", "6SHS", "6TI7", "6YEJ", "7SP1"
    };
    private static final List<String> VALID_PROTEINS = Arrays.asList(TARGET_FOLDERS);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        // 依然禁止缓存，保险起见——即便GET本身是安全的，也不希望浏览器把这个页面缓存住
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String proteinsParam = request.getParameter("proteins");
        List<String> proteins = new ArrayList<>();
        if (proteinsParam != null && !proteinsParam.trim().isEmpty()) {
            for (String p : proteinsParam.split(",")) {
                p = p.trim();
                if (VALID_PROTEINS.contains(p)) {
                    proteins.add(p);
                }
            }
        }

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<title>Batch start successful - Molecular docking system</title>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\">");
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">");
        out.println("<style>");
        out.println("body { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; }");
        out.println(".success-container { max-width: 700px; background: rgba(255,255,255,0.95); padding: 40px; border-radius: 8px; text-align: center; }");
        out.println(".success-icon { font-size: 4rem; color: #28a745; margin-bottom: 20px; }");
        out.println(".protein-list { background: #f8f9fa; padding: 20px; border-radius: 6px; margin: 20px 0; text-align: left; max-height: 300px; overflow-y: auto; }");
        out.println(".protein-row { display: flex; align-items: center; justify-content: space-between; margin: 10px 0; }");
        out.println(".protein-status { font-weight: 600; }");
        out.println(".protein-status.running { color: #b8860b; }");
        out.println(".protein-status.completed { color: #28a745; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<div class=\"success-container\">");

        if (proteins.isEmpty()) {
            out.println("<div class=\"success-icon\" style=\"color:#dc3545;\"><i class=\"fas fa-exclamation-triangle\"></i></div>");
            out.println("<h2 style=\"color: #dc3545; margin-bottom: 20px;\">No valid protein specified</h2>");
        } else {
            out.println("<div class=\"success-icon\"><i class=\"fas fa-check-circle\"></i></div>");
            out.println("<h2 style=\"color: #28a745; margin-bottom: 20px;\">Batch start successful</h2>");
            out.println("<p>Start-up was successful, " + proteins.size() + " molecular docking calculation(s)</p>");

            out.println("<div class=\"protein-list\">");
            out.println("<h5>List of proteins involved in initiation:</h5>");
            for (String protein : proteins) {
                out.println("<div class=\"protein-row\">");
                out.println("  <span><i class=\"fas fa-play text-success\"></i> Target Protein " + protein + "</span>");
                out.println("  <span class=\"protein-status running\" id=\"status-" + protein + "\">Running...</span>");
                out.println("</div>");
            }
            out.println("</div>");

            out.println("<div style=\"margin-top: 20px;\">");
            out.println("<button type=\"button\" id=\"refreshStatusBtn\" class=\"btn btn-outline-primary\">");
            out.println("<i class=\"fas fa-sync-alt\"></i> Refresh status");
            out.println("</button>");
            out.println("<span id=\"lastChecked\" style=\"margin-left: 10px; color: #6c757d; font-size: 0.85rem;\"></span>");
            out.println("</div>");
        }

        out.println("<div style=\"margin-top: 20px;\">");
        out.println("<a href=\"javascript:history.back()\" class=\"btn btn-secondary\">");
        out.println("<i class=\"fas fa-arrow-left\"></i> Back to selection</a>");
        out.println("</div>");

        out.println("</div>");

        if (!proteins.isEmpty()) {
            out.println("<script>");
            out.print("const proteins = [");
            for (int i = 0; i < proteins.size(); i++) {
                out.print("\"" + proteins.get(i) + "\"");
                if (i < proteins.size() - 1) out.print(",");
            }
            out.println("];");
            out.println("let pollTimeoutId = null;");
            out.println("function checkCompletion() {");
            out.println("  if (pollTimeoutId) { clearTimeout(pollTimeoutId); pollTimeoutId = null; }");
            out.println("  const pending = proteins.filter(p => {");
            out.println("    const el = document.getElementById('status-' + p);");
            out.println("    return el && el.textContent.trim() === 'Running...';");
            out.println("  });");
            out.println("  const lastCheckedEl = document.getElementById('lastChecked');");
            out.println("  if (pending.length === 0) {");
            out.println("    if (lastCheckedEl) lastCheckedEl.textContent = 'All done.';");
            out.println("    return;");
            out.println("  }");
            out.println("  fetch('docking-completion?proteins=' + encodeURIComponent(pending.join(',')))");
            out.println("    .then(res => res.json())");
            out.println("    .then(statusMap => {");
            out.println("      pending.forEach(p => {");
            out.println("        if (statusMap[p]) {");
            out.println("          const el = document.getElementById('status-' + p);");
            out.println("          el.textContent = 'Completed';");
            out.println("          el.classList.remove('running');");
            out.println("          el.classList.add('completed');");
            out.println("        }");
            out.println("      });");
            out.println("      if (lastCheckedEl) {");
            out.println("        const now = new Date().toLocaleTimeString();");
            out.println("        lastCheckedEl.textContent = 'Last checked: ' + now;");
            out.println("      }");
            out.println("      pollTimeoutId = setTimeout(checkCompletion, 5000);");
            out.println("    })");
            out.println("    .catch(err => {");
            out.println("      console.error('Failed to poll docking completion:', err);");
            out.println("      if (lastCheckedEl) lastCheckedEl.textContent = 'Check failed, will retry...';");
            out.println("      pollTimeoutId = setTimeout(checkCompletion, 5000);");
            out.println("    });");
            out.println("}");
            out.println("document.getElementById('refreshStatusBtn').addEventListener('click', checkCompletion);");
            out.println("checkCompletion();");
            out.println("</script>");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
