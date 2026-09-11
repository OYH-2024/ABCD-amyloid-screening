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

//@WebServlet("/infor.do")
//public class InforServlet extends ViewBaseServlet {
//    @Override
//    public void doGet(HttpServletRequest request , HttpServletResponse response)throws IOException, ServletException {
//        InforDAO inforDAO = new InforDAOImpl();
//        List<Infor> inforList = inforDAO.getInforList();
//        //保存到session作用域
//        HttpSession session = request.getSession() ;
//        session.setAttribute("inforList",inforList);
//        //此处的视图名称是 index
//        //那么thymeleaf会将这个 逻辑视图名称 对应到 物理视图 名称上去
//        //逻辑视图名称 ：   index
//        //物理视图名称 ：   view-prefix + 逻辑视图名称 + view-suffix
//        //所以真实的视图名称是：      /       index       .html
//        super.processTemplate("infor",request,response);
//    }
//}

@WebServlet("/infor2")
public class Infor2Servlet extends ViewBaseServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }

    @Override
    public void doGet(HttpServletRequest request , HttpServletResponse response)throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession() ;
        Integer pageNo = 1 ;

        String oper = request.getParameter("oper");

        String keyword = null ;
        if(StringUtil.isNotEmpty(oper) && "search".equals(oper)){
            //说明是点击表单查询发送过来的请求
            //此时，pageNo应该还原为1 ， keyword应该从请求参数中获取
            pageNo = 1 ;
            keyword = request.getParameter("keyword");
            if(StringUtil.isEmpty(keyword)){
                keyword = "" ;
            }
            session.setAttribute("keyword",keyword);
        }else{
            //说明此处不是点击表单查询发送过来的请求（比如点击下面的上一页下一页或者直接在地址栏输入网址）
            //此时keyword应该从session作用域获取
            String pageNoStr = request.getParameter("pageNo");
            if(StringUtil.isNotEmpty(pageNoStr)){
                pageNo = Integer.parseInt(pageNoStr);
            }
            Object keywordObj = session.getAttribute("keyword");
            if(keywordObj!=null){
                keyword = (String)keywordObj ;
            }else{
                keyword = "" ;
            }
        }

        session.setAttribute("pageNo",pageNo);


        InforDAO inforDAO = new InforDAOImpl();
        List<Infor> inforList = inforDAO.getInforList(keyword , pageNo);

        session.setAttribute("inforList",inforList);

        //总记录条数
        int inforCount = inforDAO.getInforCount(keyword);
        int pageCount = (inforCount+15-1)/15 ;

        session.setAttribute("pageCount",pageCount);
        super.processTemplate("infor2",request,response);




    }






}
