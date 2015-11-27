package com.jdk2010.framework.web.session.redis;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.jdk2010.framework.web.session.SessionHttpRequestWrapper;
import com.jdk2010.framework.web.session.util.SeesionSerializer;

public class RedisSessionManager {
    public static final String DEFAULT_SESSION_ID_PREFIX = "REDISID_"; // redis的key
    public static final String DEFAULT_SESSION_ID_COOKIE = "JSESSIONID"; // 默认浏览器cookie的key
    private int expirationUpdateInterval; // session最大更新间隔时间
    private int sessionTimeOut; // session过期时间
    private  Jedis jedis;

    public RedisSessionManager() {
        this.expirationUpdateInterval = 15;
        this.sessionTimeOut = 30;
    }

    public RedisSessionManager(String host, int port) {
        this(host, port, 30);
    }

    public RedisSessionManager(String host, int port, int sessionTimeOut) {
        this.expirationUpdateInterval = 15;
        this.sessionTimeOut = sessionTimeOut;
        jedis = new Jedis(host, port);
    }

    public RedisHttpSession createSession(SessionHttpRequestWrapper request, HttpServletResponse response,
            boolean create) throws UnsupportedEncodingException {
        String sessionId = getRequestedSessionId(request);
        RedisHttpSession session = null;
        // 首次登录没有SessionID，并且不创建新Session则不处理
        if ((StringUtils.isEmpty(sessionId)) && (!(create))) {
            System.out.println("首次登录没有SessionID，并且不创建新Session则不处理");
            return null;
        }
        // 如果SessionID不为空则从Redis加载Session
        if (StringUtils.isNotEmpty(sessionId)) {
            System.out.println("如果SessionID不为空则从Redis加载Session");
            session = loadSession(sessionId);
        }
        // 如果是首次登录则Session为空,生成空Session
        if ((session == null) && (create)) {
            System.out.println("如果是首次登录则Session为空,生成空Session");
            session = createEmptySession(request, response);
        }
        // 如果Session不为空则，附加各种回调事件
        if (session != null) {
            int updateInterval = (int) ((System.currentTimeMillis() - session.lastAccessedTime) / 1000);
            // 如果 Session一致 并且在最小间隔同步时间内 则不与Redis同步
            if (updateInterval > expirationUpdateInterval){
                System.out.println("当前时间-上次更新时间>最小的更新时间 则更新redis缓存");
                session.lastAccessedTime = System.currentTimeMillis();
                saveSession(session);
            }
            
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
            session = SeesionSerializer.deserialize(jedis.get(
                    generatorSessionKey(sessionId).getBytes("UTF-8")));
            // 重新加载到本地缓存的Session需要重新设置同步标志与新建标志
            return session;
        } catch (Exception e) {
            e.printStackTrace();
        }
        jedis.close();
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
        if (session.isNew == false) {
            return;
        }
        Cookie cookie = new Cookie(DEFAULT_SESSION_ID_COOKIE, null);
        cookie.setPath(request.getContextPath());
        // 如果Session过期则Cookies也过期
        if (session.expired) {
            cookie.setMaxAge(0);
            System.out.println("如果Session是新生成的，则需要在客户端设置SessionID");
            // 如果Session是新生成的，则需要在客户端设置SessionID
        } else if (session.isNew) {
            cookie.setValue(session.getId());
        }
        response.addCookie(cookie);
    }

    /**
     * 保存Session
     * 
     * @param session
     * @throws UnsupportedEncodingException
     */
    private void saveSession(RedisHttpSession session) throws UnsupportedEncodingException {
        String sessionid = generatorSessionKey(session.getId());
        if (session.expired) {
            System.out.println("过期，删除");
            jedis.del(session.getId());
        } else {
            System.out.println("保存");
            jedis.setex(sessionid.getBytes("utf-8"), this.sessionTimeOut,
                    SeesionSerializer.serialize(session));
        }
        jedis.close();
    }

    private String createSessionId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    private static String generatorSessionKey(String sessionId) {
        return DEFAULT_SESSION_ID_PREFIX.concat(sessionId);
    }

}
