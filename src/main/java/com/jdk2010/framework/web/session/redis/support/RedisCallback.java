package com.jdk2010.framework.web.session.redis.support;

import redis.clients.jedis.Jedis;

public interface RedisCallback<T> {
    public T call(Jedis jedis, Object params);
}
