package com.mqf.crm.web.filter;

import javax.servlet.*;
import java.io.IOException;

public class EncodingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //过滤post请求参数中文乱码
        servletRequest.setCharacterEncoding("utf-8");
        //过滤响应流中文乱码
        servletResponse.setContentType("text/html;charset=utf-8");
        //将请求放行
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
