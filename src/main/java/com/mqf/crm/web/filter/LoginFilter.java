package com.mqf.crm.web.filter;

import com.mqf.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        User user = (User) request.getSession().getAttribute("user");
        String path = request.getServletPath();
        //如果进入登录界面或者登录请求放行
        if ("/login.jsp".equals(path)||"/settings/user/login.do".equals(path)){
            filterChain.doFilter(servletRequest,servletResponse);
        }
        //不是登录操作要判断
        else {
            //不为空放行
            if (user != null){
                filterChain.doFilter(servletRequest,servletResponse);
            }
            //为空跳转到登录页面
            else {
                //使用重定向
                response.sendRedirect(request.getContextPath()+"/login.jsp");
            }
        }

    }
}
