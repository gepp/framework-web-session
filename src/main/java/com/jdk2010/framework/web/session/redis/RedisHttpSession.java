package com.jdk2010.framework.web.session.redis;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class RedisHttpSession implements HttpSession, Serializable {

    protected long creationTime = 0L;

    protected long lastAccessedTime = 0L;

    protected String id;

    protected ServletContext servletContext = null;

    protected int maxInactiveInterval;

    private Map<String, Object> data;

    protected transient boolean isNew; // Session新建标志

    public RedisHttpSession() {
        this.data = new ConcurrentHashMap<String, Object>();
    }

    /**
     * 返回建立session的时间，这个时间表示为自1970-1-1日（GMT）以来的毫秒数。
     */
    @Override
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * 返回分配给这个session的标识符。一个HTTP session的标识符是一个由服务器来建立和维持的唯一的字符串。
     */
    @Override
    public String getId() {
        return id;
    }
    /**
     * 返回客户端最后一次发出与这个session有关的请求的时间，如果这个session是新建立的，返回-1。这个时间表示为自1970-1-1日（GMT）以来的毫秒数
     */
    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    /**
     * 返加一个秒数，这个秒数表示客户端在不发出请求时，session被Servlet引擎维持的最长时间。在这个时间之后，Servlet引擎可能被Servlet引擎终止。如果这个session不会被终止，这个方法返回-1。当session无效后再调用这个方法会抛出一个IllegalStateException
     */
    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    /*
     * 返回session在其中得以保持的环境变量。这个方法和其他所有HttpSessionContext的方法一样被取消了
     */
    @Override
    public HttpSessionContext getSessionContext() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return this.data.get(name);
    }

    @Override
    public Object getValue(String name) {
        return this.data.get(name);
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;   //获取所有key
    }

    @Override
    public String[] getValueNames() {
        String[] names = new String[this.data.size()];
        return ((String[]) this.data.keySet().toArray(names));
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.data.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        this.data.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.data.remove(name);
    }

    @Override
    public void removeValue(String name) {
        this.data.remove(name);
    }

    /**
     * 这个方法会终止这个session。所有绑定在这个session上的数据都会被清除。并通过HttpSessionBindingListener接口的valueUnbound方法发出通告。
     */
    @Override
    public void invalidate() {
        System.out.println("===================失效==================="+this.getId());
    }

    /**
     * 返回一个布尔值以判断这个session是不是新的。如果一个session已经被服务器建立但是还没有收到相应的客户端的请求，这个session将被认为是新的。这意味着，这个客户端还没有加入会话或没有被会话公认。在他发出下一个请求时还不能返回适当的session认证信息。当session无效后再调用这个方法会抛出一个IllegalStateException。
     */
    @Override
    public boolean isNew() {
        return this.isNew;
    }

}
