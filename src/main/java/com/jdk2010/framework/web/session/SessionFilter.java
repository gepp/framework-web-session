package com.jdk2010.framework.web.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.print.resources.serviceui;

import com.jdk2010.framework.web.session.redis.RedisSessionManager;

public class SessionFilter implements Filter {

    private RedisSessionManager sessionManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        sessionManager = new RedisSessionManager("10.19.40.46", 6379);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        SessionHttpRequestWrapper requestWrapper = new SessionHttpRequestWrapper(httpServletRequest,
                httpServletResponse, this.sessionManager);

        chain.doFilter(requestWrapper, httpServletResponse);
    }

    @Override
    public void destroy() {

    }

}
