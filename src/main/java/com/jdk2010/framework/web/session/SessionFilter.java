package com.jdk2010.framework.web.session;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sun.print.resources.serviceui;

import com.jdk2010.framework.web.session.redis.RedisSessionManager;

public class SessionFilter implements Filter {

    private RedisSessionManager sessionManager;

    // 静态资源不做过滤
    public static final String[] IGNORE_SUFFIX = { ".png", ".jpg", ".jpeg", ".gif", ".css", ".js", ".html", ".htm",
            "swf" };
    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        String filePath = "/WEB-INF/classes/session.properties";
        InputStream is = null;
        try {
            is = servletContext.getResourceAsStream(filePath);
            if (is == null) {
                throw new FileNotFoundException(filePath);
            }
            Properties props = new Properties();
            props.load(is);
            String redisIp = props.getProperty("redisIp");
            int redisPort = Integer.parseInt(props.getProperty("redisPort"));
            int sessionTimeoutSeconds = Integer.parseInt(props.getProperty("sessionTimeoutSeconds"));
            int sessionIgnoreSeconds = Integer.parseInt(props.getProperty("sessionIgnoreSeconds"));
            sessionManager = new RedisSessionManager(redisIp, redisPort, sessionTimeoutSeconds, sessionIgnoreSeconds);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ignore) {
                    // ignore it
                }
            }
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (!(ifFilter(httpServletRequest))) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        SessionHttpRequestWrapper requestWrapper = new SessionHttpRequestWrapper(httpServletRequest,
                httpServletResponse, this.sessionManager);

        chain.doFilter(requestWrapper, httpServletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * 是否过滤请求
     * 
     * @param request
     * @return
     */
    private boolean ifFilter(HttpServletRequest request) {
        String uri = request.getRequestURI().toLowerCase();
        for (String suffix : IGNORE_SUFFIX) {
            if (uri.endsWith(suffix))
                return false;
        }
        return true;
    }
}
