package com.jdk2010.framework.web.session.redis;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.JedisPool;

import com.jdk2010.framework.web.session.SessionHttpRequestWrapper;
import com.jdk2010.framework.web.session.util.SeesionSerializer;

public class RedisSessionManager {
    public static final String DEFAULT_SESSION_ID_PREFIX = "REDISID_"; // redis的key
    public static final String DEFAULT_SESSION_ID_COOKIE = "JSESSIONID"; // 默认浏览器cookie的key
    private int expirationUpdateInterval; // session最大更新间隔时间
    private int sessionTimeOut; // session过期时间
    private JedisPool jedisPool;

    public RedisSessionManager() {
        this.expirationUpdateInterval = 600;
        this.sessionTimeOut = 1800;
    }

    public RedisSessionManager(String host, int port) {
        this(host, port, 1800);
    }

    public RedisSessionManager(String host, int port, int sessionTimeOut) {
        this.expirationUpdateInterval = 300;
        this.sessionTimeOut = sessionTimeOut;
        jedisPool = new JedisPool(host, port);
    }

    public RedisHttpSession createSession(SessionHttpRequestWrapper request, HttpServletResponse response,
            boolean create) {
        String sessionId = getRequestedSessionId(request);
        RedisHttpSession session = null;
        // 首次登录没有SeeionID，并且不创建新Session则不处理
        if ((StringUtils.isEmpty(sessionId)) && (!(create))) {
            return null;
        }
        // 如果SessionID不为空则从Redis加载Session
        if (StringUtils.isNotEmpty(sessionId)) {
            session = loadSession(sessionId);
        }
        // 如果是首次登录则Session为空,生成空Session
        if ((session == null) && (create)) {
            session = createEmptySession(request, response);
        }
        return session;
    }

    /**
     * 从Request的Cookies中取出SessionId
     * 
     * @param request
     * @return
     */
    private String getRequestedSessionId(HttpServletRequestWrapper request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0)
            return null;
        for (Cookie cookie : cookies) {
            if (DEFAULT_SESSION_ID_COOKIE.equals(cookie.getName()))
                return cookie.getValue();
        }
        return null;
    }

    private RedisHttpSession loadSession(String sessionId) {
        RedisHttpSession session;
        try {
            session = SeesionSerializer.deserialize(jedisPool.getResource().get(
                    generatorSessionKey(sessionId).getBytes("UTF-8")));
            // 重新加载到本地缓存的Session需要重新设置同步标志与新建标志
            return session;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private RedisHttpSession createEmptySession(SessionHttpRequestWrapper request, HttpServletResponse response) {
        RedisHttpSession session = new RedisHttpSession();
        session.id = createSessionId();
        session.creationTime = System.currentTimeMillis();
        session.maxInactiveInterval = this.sessionTimeOut;
        session.isNew = true;
        saveCookie(session, request, response);
        return session;
    }

    private void saveCookie(RedisHttpSession session, HttpServletRequestWrapper request, HttpServletResponse response) {
        if (session.isNew == false)
            return;

        Cookie cookie = new Cookie(DEFAULT_SESSION_ID_COOKIE, null);
        cookie.setPath(request.getContextPath());
        // 如果Session过期则Cookies也过期
        cookie.setValue(session.getId());
        response.addCookie(cookie);
    }
    /**
     * 保存Session
     * @param session
     * @throws UnsupportedEncodingException 
     */
    private void saveSession(RedisHttpSession session) throws UnsupportedEncodingException {
        String sessionid = generatorSessionKey(session.id);
        jedisPool.getResource().setex(sessionid.getBytes("utf-8"),  this.sessionTimeOut,SeesionSerializer.serialize(session));

    }

    private String createSessionId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private static String generatorSessionKey(String sessionId) {
        return DEFAULT_SESSION_ID_PREFIX.concat(sessionId);
    }

}
