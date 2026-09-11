package com.atguigu.fruit.servlets;

import com.atguigu.fruit.dao.InforDAO;
import com.atguigu.fruit.dao.impl.InforDAOImpl;
import com.atguigu.fruit.pojo.Infor;
import com.atguigu.myssm.myspringmvc.ViewBaseServlet;
import com.atguigu.myssm.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/infor")
public class InforServlet extends ViewBaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String oper = request.getParameter("oper");
        String targetParam = request.getParameter("target");
        String target;
        String keyword;
        Integer pageNo = 1;
//########################################################################################
        if ("search".equals(oper)) {
            // 情况B（提前判断）：搜索表单提交 —— target、keyword 都从本次请求里取
            target = StringUtil.isNotEmpty(targetParam) ? targetParam : (String) session.getAttribute("target");
            keyword = request.getParameter("keyword");
            if (StringUtil.isEmpty(keyword)) {
                keyword = "";
            }
            session.setAttribute("target", target);
            session.setAttribute("keyword", keyword);
            pageNo = 1;

        } else if (StringUtil.isNotEmpty(targetParam)) {
            // 情况A：从 Rank.html 点进来，没有 oper=search，说明是切换靶蛋白
            target = targetParam;
            keyword = "";
            session.setAttribute("target", target);
            session.setAttribute("keyword", keyword);
            pageNo = 1;

        } else {
            // 情况C：翻页（上一页/下一页/末页），target 和 keyword 都从 session 里恢复
            Object targetObj = session.getAttribute("target");
            target = (targetObj != null) ? (String) targetObj : "";

            Object keywordObj = session.getAttribute("keyword");
            keyword = (keywordObj != null) ? (String) keywordObj : "";

            String pageNoStr = request.getParameter("pageNo");
            if (StringUtil.isNotEmpty(pageNoStr)) {
                pageNo = Integer.parseInt(pageNoStr);
            }
        }
//        #####################################################

        session.setAttribute("pageNo", pageNo);
        session.setAttribute("targetName", target);

        InforDAO inforDAO = new InforDAOImpl();
        List<Infor> inforList = inforDAO.getInforListByTargetAndKeyword(target, keyword, pageNo);
        session.setAttribute("inforList", inforList);

        int inforCount = inforDAO.getInforCountByTargetAndKeyword(target, keyword);
        int pageCount = (inforCount + 15 - 1) / 15;
        session.setAttribute("pageCount", pageCount);

        super.processTemplate("infor", request, response);
    }
}