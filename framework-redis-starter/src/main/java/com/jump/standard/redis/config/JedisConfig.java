package com.jump.standard.redis.config;

import com.jump.standard.redis.service.impl.JedisClient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 〈jedis配置〉
 *
 * @author LiLin
 * @date 2020/7/2 0002
 */
@Configuration
@ConfigurationProperties(prefix = "redis")
public class JedisConfig {
    private String server;
    private int port = 6379;
    private String password;
    private boolean ssl;
    private Map<String, String> properties;

    @Bean
    public JedisClient getJedisClient(){
        JedisClient jedisClient = new JedisClient();
        jedisClient.startup(server, port, password, ssl, properties);
        return jedisClient;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}