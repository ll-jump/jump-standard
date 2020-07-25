package com.jump.standard.redis.service.impl;

import com.jump.standard.redis.service.JedisClientInterface;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

/**
 * 〈jedis工具类 redis集群〉
 *
 * @author LiLin
 * @date 2020/7/2 0002
 */
public class JedisClient implements JedisClientInterface<JedisCluster> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisClient.class);

    private JedisCluster jedisCluster;

    @Override
    public JedisCluster getConnection() {
        return jedisCluster;
    }


    @Override
    public void startup(String host, int port, String password, boolean ssl, Map<String, String> properties) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        if (properties != null) {
            DirectFieldAccessor accessor = new DirectFieldAccessor(poolConfig);
            accessor.setAutoGrowNestedPaths(true);
            Set<String> keys = properties.keySet();
            for (String key : keys) {
                try {
                    accessor.setPropertyValue(key, properties.get(key));
                } catch (BeansException ex) {
                    LOGGER.warn("非法的参数值, key={}, value={}", key, properties.get(key));
                }
            }
        }
        if (jedisCluster == null) {
            jedisCluster = new JedisCluster(toJedisClusterInfoSet(host, port), 500, 2000, 1, password, poolConfig);
        }

    }

    @Override
    public void destroy() throws Exception {
        if (jedisCluster != null) {
            jedisCluster.close();
        }
    }

    @Override
    public boolean sadd(String key, String... value) {
        if (value != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.sadd(key, value);
                LOGGER.info("set data, key={}, result={}", key, result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String spop(String key) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                String result = connection.spop(key);
                LOGGER.info("set data, key={}, result={}", key, result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean set(String key, Object value) {
        if (value != null && !StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                String result = connection.set(key.getBytes(UTF_8), KryoSerializer.serialize(value, Object.class));
                LOGGER.info("set data, key={}, result={}", key, result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean set(String key, Object value, int expire) {
        if (value != null && !StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                String result = connection.setex(key.getBytes(UTF_8), expire, KryoSerializer.serialize(value, Object.class));
                LOGGER.info("set data, key={}, expire={}, result={}", key, expire, result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean setString(String key, String value) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            JedisCluster connection = getConnection();
            try {
                String result = connection.set(key, value);
                LOGGER.info("set data, key={},  result={}", key, result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean setString(String key, String value, int expire) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            JedisCluster connection = getConnection();
            try {
                String result = connection.setex(key, expire, value);
                LOGGER.info("set data, key={},  result={}", key,  result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean setNx(String key, String value) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.setnx(key, value);
                LOGGER.info("set data, key={}, result={}", key, result);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean setNx(String key, String value, int expire) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            JedisCluster connection = getConnection();
            try {
                SetParams setParams = new SetParams();
                setParams.nx();
                setParams.px(expire*1000);
                String result = connection.set(key,value,setParams);
                LOGGER.info("set data, result={}, key={}, expire={}", result, key, expire);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public <T> boolean setList(String key, List<T> value, Class<T> clazz, int expire) {
        if (value != null && !StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                String result
                        = connection.setex(key.getBytes(UTF_8), expire, KryoSerializer.serializationList(value, clazz));
                LOGGER.info("set data, key={}, result={}, expire={}", key, result, expire);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public <T> boolean putList(String key, String field, List<T> value, Class<T> clazz, int expire) {
        if (value != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(field)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.hset(key.getBytes(UTF_8), field.getBytes(UTF_8),
                        KryoSerializer.serializationList(value, clazz));
                LOGGER.info("set data, key={}, field={}, result={}", key, field, result);
                if (expire > 0) {
                    // 设置超时时间
                    result = connection.expire(key.getBytes(UTF_8), expire);
                    LOGGER.info("set data expire, result={}, key={}, expire={}", result, key, expire);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean put(String key, String field, Object value) {
        if (value != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(field)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.hset(key.getBytes(UTF_8), field.getBytes(UTF_8),
                        KryoSerializer.serialize(value, Object.class));
                LOGGER.info("set data, key={}, field={}, result={}", key, field, result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean put(String key, String field, Object value, int expire) {
        if (value != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(field)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.hset(key.getBytes(UTF_8), field.getBytes(UTF_8),
                        KryoSerializer.serialize(value, Object.class));
                LOGGER.info("set data, key={}, field={}, result={}", key, field, result);
                // 设置超时时间
                if (expire > 0) {
                    result = connection.expire(key.getBytes(UTF_8), expire);
                    LOGGER.info("set data expire, result={}, key={}, expire={}", result, key, expire);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        if (clazz != null && !StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                byte[] bytes = connection.get(key.getBytes(UTF_8));
                if (bytes != null && bytes.length > 0) {
                    return KryoSerializer.deserialize(bytes, clazz);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> T get(String key, Class<T> clazz, int expire) {
        if (clazz != null && !StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                Long time = connection.ttl(key);
                if (time >= 0) {
                    // 更新超时时间
                    connection.expire(key, expire);
                }
                byte[] bytes = connection.get(key.getBytes(UTF_8));
                if (bytes != null && bytes.length > 0) {
                    return KryoSerializer.deserialize(bytes, clazz);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Set<String> keySet(String key) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                return connection.hkeys(key);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> T get(String key, String field, Class<T> clazz) {
        if (clazz != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(field)) {
            JedisCluster connection = getConnection();
            try {
                byte[] bytes = connection.hget(key.getBytes(UTF_8), field.getBytes(UTF_8));
                if (bytes != null && bytes.length > 0) {
                    // 反序列化
                    return KryoSerializer.deserialize(bytes, clazz);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> T get(String key, String field, Class<T> clazz, int expire) {
        if (clazz != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(field)) {
            JedisCluster connection = getConnection();
            try {
                byte[] bytes = connection.hget(key.getBytes(UTF_8), field.getBytes(UTF_8));
                if (bytes != null && bytes.length > 0) {
                    // 设置超时时间
                    if (expire > 0) {
                        Long result = connection.expire(key.getBytes(UTF_8), expire);
                        LOGGER.info("set data expire, result={}, key={}, expire={}", result, key,
                                expire);
                    }
                    // 反序列化
                    return KryoSerializer.deserialize(bytes, clazz);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getString(String key) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                return connection.get(key);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public String getString(String key, int expire) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                String value = connection.get(key);
                Long result = connection.expire(key, expire);
                LOGGER.info("set data expire, result={}, key={}, expire={}", result, key, expire);
                return value;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        if (clazz != null && !StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                byte[] bytes = connection.get(key.getBytes(UTF_8));
                if (bytes != null && bytes.length > 0) {
                    return KryoSerializer.deserializationList(bytes, clazz);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public <T> List<T> getList(String key, String field, Class<T> clazz) {
        if (clazz != null && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(field)) {
            JedisCluster connection = getConnection();
            try {
                byte[] bytes = connection.hget(key.getBytes(UTF_8), field.getBytes(UTF_8));
                if (bytes != null && bytes.length > 0) {
                    // 反序列化
                    return KryoSerializer.deserializationList(bytes, clazz);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean remove(String key) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.del(key.getBytes(UTF_8));
                LOGGER.info("delete data, key={}, result={}", key, result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean remove(String key, String field) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.hdel(key.getBytes(UTF_8), field.getBytes(UTF_8));
                LOGGER.info("delete data, key={}, field={}, result={}", key, field, result);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Long decrement(String key) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.decr(key);
                LOGGER.info("get decrement data, key={}, result={}", key, result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Long decrementBy(String key, long start) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.decrBy(key, start);
                LOGGER.info("get decrement data by started, key={}, start={}, result={}", key, start, result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Long increment(String key) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.incr(key);
                LOGGER.info("get increment data, key={}, result={}", key, result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public Long incrementBy(String key, long start) {
        if (!StringUtils.isEmpty(key)) {
            JedisCluster connection = getConnection();
            try {
                Long result = connection.incrBy(key, start);
                LOGGER.info("get increment data by started, key={}, start={}, result={}", key, start,
                        result);
                return result;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

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
    @Override
    public <T>T getTFromRedis(String key, Class<T> tClassType,int expire, Object param, Function<Object,T> f){
        T t = get(key, tClassType);
        if (t == null){
            t = f.apply(param);
            if (t != null){
                set(key, t, expire);
            }
        }
        return t;
    }

    protected Set<HostAndPort> toJedisClusterInfoSet(String host, int port) {
        if (!StringUtils.isEmpty(host)) {
            String[] cluster = host.split(";");
            if (cluster != null && cluster.length > 0) {
                Set<HostAndPort> nodes = new HashSet<>();
                for (String node : cluster) {
                    HostAndPort info = toJedisClusterInfo(node, port);
                    if (info != null) {
                        nodes.add(info);
                    }
                }
                return nodes;
            }
        }
        return null;
    }

    protected HostAndPort toJedisClusterInfo(String node, int port) {
        final int tokenLen = 2;
        String[] token = node.split(":");
        if (token != null && token.length > 0) {
            String host = token[0];
            if (token.length == tokenLen) {
                port = NumberUtils.toInt(token[1], port);
            }
            return new HostAndPort(host, port);
        }
        return null;
    }

}