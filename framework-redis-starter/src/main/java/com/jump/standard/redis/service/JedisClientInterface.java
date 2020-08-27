package com.jump.standard.redis.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import redis.clients.jedis.JedisCluster;

/**
 * 〈jedis接口〉
 *
 * @author LiLin
 * @date 2020/7/2 0002
 */
public interface JedisClientInterface<K> {
    String UTF_8 = "utf-8";

    JedisCluster getConnection();

    void startup(String host, int port, String password, boolean ssl, Map<String, String> properties);

    void destroy() throws Exception;

    boolean set(String key, Object value);

    boolean set(String key, Object value, int expire);

    boolean setString(String key, String value);

    boolean setString(String key, String value, int expire);

    boolean setNx(String key, String value);

    boolean setNx(String key, String value, int expire);

    <T> boolean setList(String key, List<T> value, Class<T> clazz, int expire);

    <T> boolean putList(String key, String field, List<T> value, Class<T> clazz, int expire);

    boolean put(String key, String field, Object value);

    boolean put(String key, String field, Object value, int expire);

    <T> T get(String key, Class<T> clazz);

    <T> T get(String key, Class<T> clazz, int expire);

    Set<String> keySet(String key);

    <T> T get(String key, String field, Class<T> clazz);

    <T> T get(String key, String field, Class<T> clazz, int expire);

    String getString(String key);

    String getString(String key, int expire);

    <T> List<T> getList(String key, Class<T> clazz);

    <T> List<T> getList(String key, String field, Class<T> clazz);

    boolean remove(String key);

    boolean remove(String key, String field);

    Long decrement(String key);

    Long decrementBy(String key, long start);

    Long increment(String key);

    Long incrementBy(String key, long start);

    /**
     * 从redis取数据，不存在则调用函数方法获取数据并缓存到redis
     * @param key rediskey
     * @param tClassType 响应数据类类型
     * @param expire redis有效时间
     * @param param 函数方法获取数据参数
     * @param f 函数方法
     * @param <T>
     * @return
     */
    <T>T getTFromRedis(String key, Class<T> tClassType,int expire, Object param, Function<Object,T> f);

    boolean sadd(String key, String... value);

    String spop(String key);

    /**
     * redis分布式锁
     * @param key
     * @param value
     * @param expireSecond
     * @param waitSecond
     * @return
     */
    boolean lock(String key, String value, int expireSecond, Long waitSecond);

    /**
     * 释放锁
     * @param key
     * @param value
     * @return
     */
    String unlock(String key, String value);
}