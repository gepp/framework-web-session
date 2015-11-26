package com.jdk2010.framework.web.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.jdk2010.framework.web.session.redis.RedisHttpSession;
import com.jdk2010.framework.web.session.redis.RedisSessionManager;

public class SessionHttpRequestWrapper extends HttpServletRequestWrapper {

    RedisHttpSession httpSession;

    private HttpServletResponse response;
    
    private RedisSessionManager redisSessionManager;

    public SessionHttpRequestWrapper(HttpServletRequest request, HttpServletResponse response,RedisSessionManager redisSessionManager) {
        super(request);
        this.response = response;
        this.redisSessionManager=redisSessionManager;
    }

    public HttpSession getSession(boolean create) {
        if (this.httpSession != null)
            return this.httpSession;
        this.httpSession = this.redisSessionManager.createSession(this,this.response,create);
        return this.httpSession;
    }

    public HttpSession getSession() {
        return getSession(true);
    }

}
