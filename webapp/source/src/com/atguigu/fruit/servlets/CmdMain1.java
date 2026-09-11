package com.atguigu.fruit.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@WebServlet("/hello")
public class CmdMain1 extends HttpServlet {
    @Override

            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(PanDuan())
        {
//            response.setContentType("text/html");
//                PrintWriter out = response.getWriter();
//                out.println("<html>");
//                out.println("<head>");
//                out.println("<title>Docking</title>");
//                out.println("</head>");
//                out.println("<body style=\"background-color:#F0FFF0;\">");
//                out.println("<h2>Docking underway...</h2>");
//                //out.println("<h1></h1>");
//                out.println("<a href=\"judge\" >Click to check whether the molecular docking is complete.</a>" );
//
//                out.println("</body>");
//                out.println("</html>");
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("<title>分子对接进行中 - 抑制分子信息系统</title>");
            out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\">");
            out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">");
            out.println("<style>");
            out.println(":root {");
            out.println("    --primary-color: #2a5298;");
            out.println("    --secondary-color: #1e3c72;");
            out.println("    --accent-color: #00bcd4;");
            out.println("    --success-color: #4caf50;");
            out.println("    --warning-color: #ff9800;");
            out.println("    --light-bg: #f8f9fa;");
            out.println("    --dark-text: #333;");
            out.println("    --light-text: #fff;");
            out.println("    --border-radius: 8px;");
            out.println("    --box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);");
            out.println("}");

            out.println("body {");
            out.println("    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
            out.println("    min-height: 100vh;");
            out.println("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
            out.println("    margin: 0;");
            out.println("    padding: 0;");
            out.println("    display: flex;");
            out.println("    align-items: center;");
            out.println("    justify-content: center;");
            out.println("}");

            out.println(".status-container {");
            out.println("    max-width: 600px;");
            out.println("    width: 90%;");
            out.println("    background: rgba(255, 255, 255, 0.95);");
            out.println("    border-radius: var(--border-radius);");
            out.println("    box-shadow: var(--box-shadow);");
            out.println("    padding: 40px;");
            out.println("    text-align: center;");
            out.println("    backdrop-filter: blur(10px);");
            out.println("    border: 1px solid rgba(255, 255, 255, 0.2);");
            out.println("}");

            out.println(".status-icon {");
            out.println("    font-size: 4rem;");
            out.println("    color: var(--accent-color);");
            out.println("    margin-bottom: 20px;");
            out.println("    animation: pulse 2s infinite;");
            out.println("}");

            out.println("@keyframes pulse {");
            out.println("    0%, 100% { transform: scale(1); opacity: 1; }");
            out.println("    50% { transform: scale(1.1); opacity: 0.7; }");
            out.println("}");

            out.println(".status-title {");
            out.println("    color: var(--primary-color);");
            out.println("    font-size: 2.2rem;");
            out.println("    font-weight: 700;");
            out.println("    margin-bottom: 15px;");
            out.println("}");

            out.println(".status-message {");
            out.println("    color: #666;");
            out.println("    font-size: 1.2rem;");
            out.println("    margin-bottom: 30px;");
            out.println("    line-height: 1.6;");
            out.println("}");

            out.println(".progress-section {");
            out.println("    margin: 30px 0;");
            out.println("}");

            out.println(".progress-bar-container {");
            out.println("    background: #e0e0e0;");
            out.println("    height: 8px;");
            out.println("    border-radius: 4px;");
            out.println("    margin: 20px 0;");
            out.println("    overflow: hidden;");
            out.println("}");

            out.println(".progress-bar {");
            out.println("    height: 100%;");
            out.println("    background: linear-gradient(90deg, var(--accent-color), var(--primary-color));");
            out.println("    width: 0%;");
            out.println("    animation: loading 3s ease-in-out infinite;");
            out.println("}");

            out.println("@keyframes loading {");
            out.println("    0% { width: 0%; }");
            out.println("    50% { width: 70%; }");
            out.println("    100% { width: 100%; }");
            out.println("}");

            out.println(".loading-dots {");
            out.println("    display: inline-block;");
            out.println("    font-size: 1.5rem;");
            out.println("    color: var(--accent-color);");
            out.println("}");

            out.println(".loading-dots::after {");
            out.println("    content: '';");
            out.println("    animation: dots 1.5s infinite;");
            out.println("}");

            out.println("@keyframes dots {");
            out.println("    0%, 20% { content: ''; }");
            out.println("    40% { content: '.'; }");
            out.println("    60% { content: '..'; }");
            out.println("    80%, 100% { content: '...'; }");
            out.println("}");

            out.println(".check-button {");
            out.println("    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));");
            out.println("    color: var(--light-text);");
            out.println("    border: none;");
            out.println("    padding: 15px 35px;");
            out.println("    border-radius: var(--border-radius);");
            out.println("    font-size: 1.1rem;");
            out.println("    font-weight: 600;");
            out.println("    text-decoration: none;");
            out.println("    display: inline-flex;");
            out.println("    align-items: center;");
            out.println("    gap: 10px;");
            out.println("    transition: all 0.3s ease;");
            out.println("    box-shadow: 0 4px 15px rgba(42, 82, 152, 0.3);");
            out.println("}");

            out.println(".check-button:hover {");
            out.println("    transform: translateY(-2px);");
            out.println("    box-shadow: 0 6px 20px rgba(42, 82, 152, 0.4);");
            out.println("    color: var(--light-text);");
            out.println("    text-decoration: none;");
            out.println("}");

            out.println(".info-box {");
            out.println("    background: rgba(0, 188, 212, 0.1);");
            out.println("    border: 1px solid var(--accent-color);");
            out.println("    border-radius: var(--border-radius);");
            out.println("    padding: 20px;");
            out.println("    margin: 30px 0;");
            out.println("    color: var(--dark-text);");
            out.println("}");

            out.println(".info-box h4 {");
            out.println("    color: var(--primary-color);");
            out.println("    margin-bottom: 10px;");
            out.println("    display: flex;");
            out.println("    align-items: center;");
            out.println("    gap: 8px;");
            out.println("}");

            out.println("@media (max-width: 768px) {");
            out.println("    .status-container { padding: 30px 20px; }");
            out.println("    .status-title { font-size: 1.8rem; }");
            out.println("    .status-message { font-size: 1rem; }");
            out.println("    .check-button { width: 100%; justify-content: center; }");
            out.println("}");

            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div class=\"status-container\">");
            out.println("    <div class=\"status-icon\">");
            out.println("        <i class=\"fas fa-cog fa-spin\"></i>");
            out.println("    </div>");
            out.println("    <h1 class=\"status-title\">分子对接进行中</h1>");
            out.println("    <p class=\"status-message\">");
            out.println("        系统正在进行分子对接计算，请耐心等待<span class=\"loading-dots\"></span>");
            out.println("    </p>");
            out.println("    ");
            out.println("    <div class=\"progress-section\">");
            out.println("        <div class=\"progress-bar-container\">");
            out.println("            <div class=\"progress-bar\"></div>");
            out.println("        </div>");
            out.println("    </div>");
            out.println("    ");
            out.println("    <div class=\"info-box\">");
            out.println("        <h4>");
            out.println("            <i class=\"fas fa-info-circle\"></i>");
            out.println("            温馨提示");
            out.println("        </h4>");
            out.println("        <p>分子对接计算需要一定时间，请点击下方按钮检查计算进度。</p>");
            out.println("    </div>");
            out.println("    ");
            out.println("    <a href=\"judge\" class=\"check-button\">");
            out.println("        <i class=\"fas fa-search\"></i>");
            out.println("        检查对接状态");
            out.println("    </a>");
            out.println("</div>");

            out.println("<script>");
            out.println("// 自动刷新检查状态（可选）");
            out.println("setTimeout(function() {");
            out.println("    document.querySelector('.check-button').style.animation = 'pulse 1s infinite';");
            out.println("}, 5000);");
            out.println("</script>");

            out.println("</body>");
            out.println("</html>");

            Cmdo();}else {

//                response.setContentType("text/html");
//                PrintWriter out = response.getWriter();
//                out.println("<html>");
//                out.println("<head>");
//                out.println("<title>Docking</title>");
//                out.println("</head>");
//                out.println("<body style=\"background-color:#F0FFF0;\">");
//                out.println("<h2>Molecular docking failed, please wait for the completion of molecular docking with others, and then restart the docking...</h2>");
//                //out.println("<h1></h1>");
//                out.println("<a href=\"judge\" >Click to check whether the molecular docking is complete.</a>" );
//
//                out.println("</body>");
//                out.println("</html>");
//            System.out.println("-----等待吧，朋友-----");

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"UTF-8\">");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("<title>分子对接状态 - 抑制分子信息系统</title>");
            out.println("<link rel=\"stylesheet\" href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\">");
            out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css\">");
            out.println("<style>");
            out.println(":root {");
            out.println("    --primary-color: #2a5298;");
            out.println("    --secondary-color: #1e3c72;");
            out.println("    --accent-color: #00bcd4;");
            out.println("    --warning-color: #ff9800;");
            out.println("    --error-color: #f44336;");
            out.println("    --light-bg: #f8f9fa;");
            out.println("    --dark-text: #333;");
            out.println("    --light-text: #fff;");
            out.println("    --border-radius: 8px;");
            out.println("    --box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);");
            out.println("}");

            out.println("body {");
            out.println("    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
            out.println("    min-height: 100vh;");
            out.println("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
            out.println("    margin: 0;");
            out.println("    padding: 20px 0;");
            out.println("    display: flex;");
            out.println("    align-items: center;");
            out.println("    justify-content: center;");
            out.println("}");

            out.println(".status-container {");
            out.println("    max-width: 700px;");
            out.println("    width: 90%;");
            out.println("    background: rgba(255, 255, 255, 0.95);");
            out.println("    border-radius: var(--border-radius);");
            out.println("    box-shadow: var(--box-shadow);");
            out.println("    padding: 40px;");
            out.println("    text-align: center;");
            out.println("    backdrop-filter: blur(10px);");
            out.println("    border: 1px solid rgba(255, 255, 255, 0.2);");
            out.println("}");

            out.println(".status-icon {");
            out.println("    font-size: 4rem;");
            out.println("    color: var(--warning-color);");
            out.println("    margin-bottom: 20px;");
            out.println("    animation: shake 0.5s ease-in-out;");
            out.println("}");

            out.println("@keyframes shake {");
            out.println("    0%, 100% { transform: translateX(0); }");
            out.println("    25% { transform: translateX(-5px); }");
            out.println("    75% { transform: translateX(5px); }");
            out.println("}");

            out.println(".status-title {");
            out.println("    color: var(--error-color);");
            out.println("    font-size: 2.2rem;");
            out.println("    font-weight: 700;");
            out.println("    margin-bottom: 20px;");
            out.println("}");

            out.println(".status-message {");
            out.println("    color: #666;");
            out.println("    font-size: 1.1rem;");
            out.println("    margin-bottom: 30px;");
            out.println("    line-height: 1.6;");
            out.println("    padding: 0 20px;");
            out.println("}");

            out.println(".warning-box {");
            out.println("    background: rgba(255, 152, 0, 0.1);");
            out.println("    border: 1px solid var(--warning-color);");
            out.println("    border-radius: var(--border-radius);");
            out.println("    padding: 25px;");
            out.println("    margin: 30px 0;");
            out.println("    text-align: left;");
            out.println("}");

            out.println(".warning-box h4 {");
            out.println("    color: var(--warning-color);");
            out.println("    margin-bottom: 15px;");
            out.println("    display: flex;");
            out.println("    align-items: center;");
            out.println("    gap: 10px;");
            out.println("    font-size: 1.3rem;");
            out.println("}");

            out.println(".warning-list {");
            out.println("    list-style: none;");
            out.println("    padding: 0;");
            out.println("    margin: 0;");
            out.println("}");

            out.println(".warning-list li {");
            out.println("    padding: 8px 0;");
            out.println("    display: flex;");
            out.println("    align-items: center;");
            out.println("    color: var(--dark-text);");
            out.println("}");

            out.println(".warning-list li::before {");
            out.println("    content: '▶';");
            out.println("    color: var(--warning-color);");
            out.println("    font-weight: bold;");
            out.println("    margin-right: 10px;");
            out.println("}");

            out.println(".action-buttons {");
            out.println("    display: flex;");
            out.println("    gap: 20px;");
            out.println("    justify-content: center;");
            out.println("    flex-wrap: wrap;");
            out.println("    margin-top: 30px;");
            out.println("}");

            out.println(".check-button {");
            out.println("    background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));");
            out.println("    color: var(--light-text);");
            out.println("    border: none;");
            out.println("    padding: 15px 30px;");
            out.println("    border-radius: var(--border-radius);");
            out.println("    font-size: 1.1rem;");
            out.println("    font-weight: 600;");
            out.println("    text-decoration: none;");
            out.println("    display: inline-flex;");
            out.println("    align-items: center;");
            out.println("    gap: 10px;");
            out.println("    transition: all 0.3s ease;");
            out.println("    box-shadow: 0 4px 15px rgba(42, 82, 152, 0.3);");
            out.println("    min-width: 150px;");
            out.println("    justify-content: center;");
            out.println("}");

            out.println(".check-button:hover {");
            out.println("    transform: translateY(-2px);");
            out.println("    box-shadow: 0 6px 20px rgba(42, 82, 152, 0.4);");
            out.println("    color: var(--light-text);");
            out.println("    text-decoration: none;");
            out.println("}");

            out.println(".back-button {");
            out.println("    background: rgba(255, 255, 255, 0.9);");
            out.println("    color: var(--accent-color);");
            out.println("    border: 2px solid var(--accent-color);");
            out.println("    padding: 15px 30px;");
            out.println("    border-radius: var(--border-radius);");
            out.println("    font-size: 1.1rem;");
            out.println("    font-weight: 600;");
            out.println("    text-decoration: none;");
            out.println("    display: inline-flex;");
            out.println("    align-items: center;");
            out.println("    gap: 10px;");
            out.println("    transition: all 0.3s ease;");
            out.println("    min-width: 150px;");
            out.println("    justify-content: center;");
            out.println("}");

            out.println(".back-button:hover {");
            out.println("    background: var(--accent-color);");
            out.println("    color: var(--light-text);");
            out.println("    transform: translateY(-1px);");
            out.println("    text-decoration: none;");
            out.println("}");

            out.println(".status-indicator {");
            out.println("    display: inline-block;");
            out.println("    width: 12px;");
            out.println("    height: 12px;");
            out.println("    border-radius: 50%;");
            out.println("    background: var(--warning-color);");
            out.println("    margin-right: 8px;");
            out.println("    animation: blink 1s infinite;");
            out.println("}");

            out.println("@keyframes blink {");
            out.println("    0%, 50% { opacity: 1; }");
            out.println("    51%, 100% { opacity: 0.3; }");
            out.println("}");

            out.println("@media (max-width: 768px) {");
            out.println("    .status-container { padding: 30px 20px; }");
            out.println("    .status-title { font-size: 1.8rem; }");
            out.println("    .status-message { font-size: 1rem; }");
            out.println("    .action-buttons { flex-direction: column; align-items: center; }");
            out.println("    .check-button, .back-button { width: 100%; max-width: 250px; }");
            out.println("}");

            out.println("</style>");
            out.println("</head>");
            out.println("<body>");

            out.println("<div class=\"status-container\">");
            out.println("    <div class=\"status-icon\">");
            out.println("        <i class=\"fas fa-exclamation-triangle\"></i>");
            out.println("    </div>");
            out.println("    <h1 class=\"status-title\">");
            out.println("        <span class=\"status-indicator\"></span>");
            out.println("        分子对接暂时失败");
            out.println("    </h1>");
            out.println("    <p class=\"status-message\">");
            out.println("        当前分子对接计算未能成功完成，请等待其他任务完成后重新启动对接程序。");
            out.println("    </p>");
            out.println("    ");
            out.println("    <div class=\"warning-box\">");
            out.println("        <h4>");
            out.println("            <i class=\"fas fa-lightbulb\"></i>");
            out.println("            可能的解决方案");
            out.println("        </h4>");
            out.println("        <ul class=\"warning-list\">");
            out.println("            <li>等待系统其他分子对接任务完成</li>");
            out.println("            <li>检查上传的PDBQT文件格式是否正确</li>");
            out.println("            <li>确认服务器资源是否充足</li>");
            out.println("            <li>稍后重新尝试启动分子对接</li>");
            out.println("        </ul>");
            out.println("    </div>");
            out.println("    ");
            out.println("    <div class=\"action-buttons\">");
            out.println("        <a href=\"judge\" class=\"check-button\">");
            out.println("            <i class=\"fas fa-redo\"></i>");
            out.println("            重新检查状态");
            out.println("        </a>");
            out.println("        <a href=\"javascript:history.back()\" class=\"back-button\">");
            out.println("            <i class=\"fas fa-arrow-left\"></i>");
            out.println("            返回主页");
            out.println("        </a>");
            out.println("    </div>");
            out.println("</div>");

            out.println("<script>");
            out.println("// 自动提示重试");
            out.println("setTimeout(function() {");
            out.println("    const checkButton = document.querySelector('.check-button');");
            out.println("    if (checkButton) {");
            out.println("        checkButton.style.animation = 'pulse 1s infinite';");
            out.println("    }");
            out.println("}, 10000);");

            out.println("// 添加点击反馈");
            out.println("document.querySelectorAll('.check-button, .back-button').forEach(button => {");
            out.println("    button.addEventListener('click', function() {");
            out.println("        this.style.transform = 'scale(0.95)';");
            out.println("        setTimeout(() => {");
            out.println("            this.style.transform = '';");
            out.println("        }, 100);");
            out.println("    });");
            out.println("});");
            out.println("</script>");

            out.println("</body>");
            out.println("</html>");

            }

//                try
//                {
//                    Thread.currentThread().sleep(80000);//毫秒,1000毫秒=1秒
//                }
//                catch(Exception e){}

                //Delete();
                //Copy();


                //TimeOpen();
        }

    public void TimeOpen(){//暂时不用这个函数了，copy那一套写在dock.bat里面了
// 需要定时执行的任务
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("-----定时测试-----");
                Delete();
                Copy();
            }
        };
        // Timer定时器工具
        Timer timer = new Timer();
        // 延迟(首次执行的时间)
        long delay = 0;
        // 间隔周期(/毫秒数)
        long intevalPeriod = 50* 1000;
        // 立即执行，并且每50秒执行一次
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }









        public void Cmdo(){

            try {
                Runtime mt = Runtime.getRuntime();
//                String cmd = "cmd /C start cmd /c \"cd/d C:\\AutodockVina\\AutodockVina && dock\"";
                String cmd = "cmd /C start cmd /c \"cd/d D:\\AutodockVina && dock\"";
//                String cmd = "cmd /C start cmd /c \"cd/d C:\\AutodockVina\\AutodockVina && dock\"";
//                String cmd = "cmd /C start cmd /c \"cd/d C:\\AutodockVina\\AutodockVina && dock\"";
                Process pro = mt.exec(cmd);
                InputStream ers= pro.getErrorStream();
                pro.waitFor();

//                //Process pro=null;
//               // ps=mt.exec(cmd);
//                int i = pro.exitValue();
//
//                for(;;){
//                    if (i!=0){
//                        break;
//                    }
//
//                }


            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            }


public void Delete(){
//    try
//    {
//        Thread.currentThread().sleep(1600000);//毫秒,1000毫秒=1秒
//    }
//    catch(Exception e){}
    File file = new File("D:\\AutodockVina\\AutodockVina");
    File[] files = file.listFiles();

    for(int i=0; i<files.length; i++){
        File tmp = files[i];
        if(tmp.toString().endsWith(".pdbqt")){
            tmp.delete();
        }
    }}


    public void Copy(){

        try {
            Runtime mt = Runtime.getRuntime();
            String cmd = "cmd /C start cmd /c copy D:\\AutodockVina\\AutodockVina\\6RHY\\6RHY.pdbqt D:\\AutodockVina\\AutodockVina";
            Process pro = mt.exec(cmd);
            InputStream ers= pro.getErrorStream();
            pro.waitFor();



        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public boolean PanDuan(){

        boolean a=true;
        try {
            Process pro = Runtime.getRuntime().exec("cmd.exe /c tasklist |find \"vina123.exe\"");
            BufferedReader reader=new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String str=reader.readLine();

            if (str!=null)
            {
                System.out.println("1.exe在运行");
                a=false;
            }else{
                System.out.println("1.exe没有在运行");
                a=true;
            }

            reader.close();
        } catch (IOException ex) {
        }



        return a;



    }



    }























