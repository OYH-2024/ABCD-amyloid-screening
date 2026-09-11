package com.atguigu.fruit.servlets;

import com.atguigu.fruit.dao.FruitDAO;
import com.atguigu.fruit.dao.impl.FruitDAOImpl;
import com.atguigu.fruit.pojo.Fruit;
import com.atguigu.myssm.myspringmvc.ViewBaseServlet;
import com.atguigu.myssm.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

//Servlet从3.0版本开始支持注解方式的注册
/*
@WebServlet("/index")
public class IndexServlet extends ViewBaseServlet {
    @Override
    public void doGet(HttpServletRequest request , HttpServletResponse response)throws IOException, ServletException {
        FruitDAO fruitDAO = new FruitDAOImpl();
        List<Fruit> fruitList = fruitDAO.getFruitList();
        //保存到session作用域
        HttpSession session = request.getSession() ;
        session.setAttribute("fruitList",fruitList);
        //此处的视图名称是 index
        //那么thymeleaf会将这个 逻辑视图名称 对应到 物理视图 名称上去
        //逻辑视图名称 ：   index
        //物理视图名称 ：   view-prefix + 逻辑视图名称 + view-suffix
        //所以真实的视图名称是：      /       index       .html
        super.processTemplate("index",request,response);
    }
}*/
/*2025年8月26日*/
//@WebServlet({"/index", "/index2"})
//public class IndexServlet extends ViewBaseServlet {
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        doGet(req,resp);
//    }
//
//
//
//    @Override
//    public void doGet(HttpServletRequest request , HttpServletResponse response)throws IOException, ServletException {
//        request.setCharacterEncoding("UTF-8");
//        HttpSession session = request.getSession() ;
//        Integer pageNo = 1 ;
//
//        String oper = request.getParameter("oper");
//        //如果oper!=null 说明 通过表单的查询按钮点击过来的
//        //如果oper是空的，说明 不是通过表单的查询按钮点击过来的
//
//        String keyword = null ;
//
//
//
//       if (StringUtil.isNotEmpty(oper) && "reset".equals(oper)) {
//            System.out.println("收到重置请求"); // 添加调试日志
//            // 清除session中的搜索关键字
//            session.removeAttribute("keyword");
//            keyword = "";
//            pageNo = 1;
//        } else if(StringUtil.isNotEmpty(oper) && "search".equals(oper)){
//            //说明是点击表单查询发送过来的请求
//            //此时，pageNo应该还原为1 ， keyword应该从请求参数中获取
//            pageNo = 1 ;
//            keyword = request.getParameter("keyword");
//            if(StringUtil.isEmpty(keyword)){
//                keyword = "" ;
//            }
//            session.setAttribute("keyword",keyword);
//        }else{
//            //说明此处不是点击表单查询发送过来的请求（比如点击下面的上一页下一页或者直接在地址栏输入网址）
//            //此时keyword应该从session作用域获取
//            String pageNoStr = request.getParameter("pageNo");
//            if(StringUtil.isNotEmpty(pageNoStr)){
//                pageNo = Integer.parseInt(pageNoStr);
//            }
//            Object keywordObj = session.getAttribute("keyword");
//            if(keywordObj!=null){
//                keyword = (String)keywordObj ;
//            }else{
//                keyword = "" ;
//            }
//        }
//
//        session.setAttribute("pageNo",pageNo);
//
//        FruitDAO fruitDAO = new FruitDAOImpl();
//
//        List<Fruit> fruitList = fruitDAO.getFruitList(keyword , pageNo);
//        session.setAttribute("fruitList",fruitList);
//
//        /*这是一个复制体*//*这是一个复制体*/
//        List<Fruit> fruitList2 = fruitDAO.getFruitList2(keyword , pageNo);
//        session.setAttribute("fruitList2",fruitList2);
//        /*这是一个复制体*//*这是一个复制体*/
//
//        //总记录条数
//        int fruitCount = fruitDAO.getFruitCount(keyword);
//        //总页数
//        int pageCount = (fruitCount+15-1)/15 ;
//        /*
//        总记录条数       总页数
//        1               1
//        5               1
//        6               2
//        10              2
//        11              3
//        fruitCount      (fruitCount+5-1)/5
//         */
//        session.setAttribute("pageCount",pageCount);
//            /*这是一个复制体*//*这是一个复制体*/
//        int fruitCount2 = fruitDAO.getFruitCount2(keyword);
//        int pageCount2 = (fruitCount2+15-1)/15 ;
//        session.setAttribute("pageCount2",pageCount2);
//           /*这是一个复制体*//*这是一个复制体*/
//
//        //此处的视图名称是 index
//        //那么thymeleaf会将这个 逻辑视图名称 对应到 物理视图 名称上去
//        //逻辑视图名称 ：   index
//        //物理视图名称 ：   view-prefix + 逻辑视图名称 + view-suffix
//        //所以真实的视图名称是：      /       index       .html
//
//        super.processTemplate("index",request,response);
//    }
//
//
//}
/*2025年8月26日更新*/
/*2025年8月26日更新*/
/*2025年8月26日更新*/
/*2025年8月26日更新*/
/*2025年8月26日更新*/
//@WebServlet({"/index", "/index2"})
@WebServlet({"/index", "/index2"})
public class IndexServlet extends ViewBaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        String servletPath = request.getServletPath();

        if ("/index2".equals(servletPath)) {
            handleIndex2Page(request, response);
        } else {
            handleIndexPage(request, response);
        }
    }

    /**
     * /index —— 按 desease 过滤（原有逻辑，不变）
     */
    private void handleIndexPage(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession();

        String oper = request.getParameter("oper");
        String deseaseParam = request.getParameter("desease");
        String desease;
        String keyword;
        Integer pageNo = 1;

        if ("search".equals(oper)) {
            desease = StringUtil.isNotEmpty(deseaseParam) ? deseaseParam : (String) session.getAttribute("desease");
            keyword = request.getParameter("keyword");
            if (StringUtil.isEmpty(keyword)) keyword = "";
            session.setAttribute("desease", desease);
            session.setAttribute("keyword", keyword);
            pageNo = 1;

        } else if ("reset".equals(oper)) {
            desease = (String) session.getAttribute("desease");
            keyword = "";
            session.setAttribute("keyword", keyword);
            pageNo = 1;

        } else if (StringUtil.isNotEmpty(deseaseParam)) {
            desease = deseaseParam;
            keyword = "";
            session.setAttribute("desease", desease);
            session.setAttribute("keyword", keyword);
            pageNo = 1;

        } else {
            Object deseaseObj = session.getAttribute("desease");
            desease = (deseaseObj != null) ? (String) deseaseObj : "";

            Object keywordObj = session.getAttribute("keyword");
            keyword = (keywordObj != null) ? (String) keywordObj : "";

            String pageNoStr = request.getParameter("pageNo");
            if (StringUtil.isNotEmpty(pageNoStr)) {
                pageNo = Integer.parseInt(pageNoStr);
            }
        }

        session.setAttribute("pageNo", pageNo);
        session.setAttribute("deseaseName", desease);

        FruitDAO fruitDAO = new FruitDAOImpl();
        List<Fruit> fruitList = fruitDAO.getFruitListByDeseaseAndKeyword(desease, keyword, pageNo);
        session.setAttribute("fruitList", fruitList);

        int fruitCount = fruitDAO.getFruitCountByDeseaseAndKeyword(desease, keyword);
        int pageCount = (fruitCount + 15 - 1) / 15;
        session.setAttribute("pageCount", pageCount);

        super.processTemplate("index", request, response);
    }

    /**
     * /index2 —— 显示全部数据，不按 desease 过滤
     */
    private void handleIndex2Page(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession();

        String oper = request.getParameter("oper");
        String keyword;
        Integer pageNo = 1;

        if ("search".equals(oper)) {
            keyword = request.getParameter("keyword");
            if (StringUtil.isEmpty(keyword)) keyword = "";
            session.setAttribute("keywordAll", keyword);
            pageNo = 1;

        } else if ("reset".equals(oper)) {
            keyword = "";
            session.setAttribute("keywordAll", keyword);
            pageNo = 1;

        } else {
            Object keywordObj = session.getAttribute("keywordAll");
            keyword = (keywordObj != null) ? (String) keywordObj : "";

            String pageNoStr = request.getParameter("pageNo");
            if (StringUtil.isNotEmpty(pageNoStr)) {
                pageNo = Integer.parseInt(pageNoStr);
            }
        }

        session.setAttribute("pageNoAll", pageNo);

        FruitDAO fruitDAO = new FruitDAOImpl();
        List<Fruit> fruitListAll = fruitDAO.getFruitListAll(keyword, pageNo);
        session.setAttribute("fruitListAll", fruitListAll);

        int fruitCountAll = fruitDAO.getFruitCountAll(keyword);
        int pageCountAll = (fruitCountAll + 15 - 1) / 15;
        session.setAttribute("pageCountAll", pageCountAll);

        super.processTemplate("index2", request, response);
    }
}