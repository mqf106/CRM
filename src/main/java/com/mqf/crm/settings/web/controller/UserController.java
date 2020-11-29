package com.mqf.crm.settings.web.controller;

import com.mqf.crm.settings.domain.User;
import com.mqf.crm.settings.service.UserService;
import com.mqf.crm.settings.service.impl.UserServiceImpl;
import com.mqf.crm.utils.MD5Util;
import com.mqf.crm.utils.PrintJson;
import com.mqf.crm.utils.ServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if (false){

        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        //验证登录
        //接收前台传来的参数
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //System.out.println(loginAct+loginPwd);
        //将接收到的密码转为密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        String ip = request.getRemoteAddr();
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        //调用业务层方法，返回一个User对象
        try{
            //如果login()方法有异常了
            User user = us.login(loginAct,loginPwd,ip);
            //如果程序执行到setAttribute说明没有抛出异常，可以得到对象user
            request.getSession().setAttribute("user",user);
            PrintJson.printJsonFlag(response,true);
        }catch (Exception e){
            e.printStackTrace();
            //取得错误信息
            String msg = e.getMessage();
            Map<String,Object> map = new HashMap<>();
            map.put("success",false);
            map.put("msg",msg);
            //将map解析为Json串打回给前端
            response.setContentType("application/json;charset=utf-8");
            PrintJson.printJsonObj(response,map);
        }
    }
}
