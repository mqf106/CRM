package com.mqf.crm.workbench.web.controller;

import com.mqf.crm.settings.domain.User;
import com.mqf.crm.settings.service.UserService;
import com.mqf.crm.settings.service.impl.UserServiceImpl;
import com.mqf.crm.utils.PrintJson;
import com.mqf.crm.utils.ServiceFactory;
import com.mqf.crm.workbench.service.CustomerService;
import com.mqf.crm.workbench.service.impl.CustomerServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if ("/workbench/transaction/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        CustomerService customerService = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> customerNameList =  customerService.getCustomerName(name);
        PrintJson.printJsonObj(response,customerNameList);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //取用户信息列表
        UserService userService = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> userList = userService.getUserList();
        request.setAttribute("userList",userList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
