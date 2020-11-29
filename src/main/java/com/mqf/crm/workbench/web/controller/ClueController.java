package com.mqf.crm.workbench.web.controller;

import com.mqf.crm.settings.domain.User;
import com.mqf.crm.settings.service.UserService;
import com.mqf.crm.settings.service.impl.UserServiceImpl;
import com.mqf.crm.utils.DateTimeUtil;
import com.mqf.crm.utils.PrintJson;
import com.mqf.crm.utils.ServiceFactory;
import com.mqf.crm.utils.UUIDUtil;
import com.mqf.crm.vo.PaginationVO;
import com.mqf.crm.workbench.domain.Activity;
import com.mqf.crm.workbench.domain.ActivityRemark;
import com.mqf.crm.workbench.service.ActivityService;
import com.mqf.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到线索控制器");
        String path = request.getServletPath();
        if ("/workbench/clue/getUserList.do".equals(path)){
            //getUserList(request,response);
        }else if ("/workbench/clue/save.do".equals(path)){
            //save(request,response);
        }
    }
}
