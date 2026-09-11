package com.atguigu.fruit.servlets;

import com.atguigu.fruit.dao.FruitDAO;
import com.atguigu.fruit.dao.impl.FruitDAOImpl;
import com.atguigu.fruit.pojo.Fruit;
import com.atguigu.myssm.myspringmvc.ViewBaseServlet;
import com.atguigu.myssm.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/detail.do")
public class DetailServlet extends ViewBaseServlet {

    private FruitDAO fruitDAO = new FruitDAOImpl();
    //本代码可以获取到表格中一条结果的完整记录。
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tidStr = request.getParameter("tid");
        if(StringUtil.isNotEmpty(tidStr)) {
            int tid = Integer.parseInt(tidStr);
            Fruit fruit = fruitDAO.getFruitByFid(tid);
            request.setAttribute("fruit",fruit);
            super.processTemplate("detail",request,response);

        }

    }
}
