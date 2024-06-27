package com.John.reggie.filter;

import java.io.IOException;
import org.springframework.util.AntPathMatcher;

import com.John.reggie.Common.BaseContext;
import com.John.reggie.Common.R;
import com.alibaba.fastjson.JSON;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter{
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    static final String[] urls = new String[]{
        "/employee/login",
        "/employee/logout",
        "/backend/**",
        "/front/**",
        "/user/sendMsg",
        "/user/login"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // log.info("Receive request: {}",req.getRequestURL());

        // To filter out
        String requestUri = req.getRequestURI();
        boolean check = check(requestUri, urls);
        if(check){
            chain.doFilter(req, res);
            // log.info("LOGIN NO NEED");
            return;
        }

        // Employee
        Long currId = (Long) req.getSession().getAttribute("employee");
        if(currId != null){
            // Store id in thread
            BaseContext.setCurrentId(currId);
            chain.doFilter(req, res);
            // log.info("ALREADY LOGIN, ID = {}",req.getSession().getAttribute("employee"));
            return;
        }

        // User
        Long currIdUser = (Long) req.getSession().getAttribute("user");
        if(currIdUser != null){
            // Store id in thread
            BaseContext.setCurrentId(currIdUser);
            chain.doFilter(req, res);
            // log.info("ALREADY LOGIN, ID = {}",req.getSession().getAttribute("employee"));
            return;
        }

        res.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("LOGIN REQUIRED");
        return;
        
    }

    public boolean check(String requestUri, String[] urls){
        for(String url: urls){
            if(PATH_MATCHER.match(url, requestUri)) return true;
        }
        return false;
    }
    
}
