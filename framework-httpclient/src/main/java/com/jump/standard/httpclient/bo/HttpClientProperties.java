package com.jump.standard.httpclient.bo;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * 〈httpClient 配置参数对象〉
 *
 * @author LiLin
 * @date 2020/6/30 0030
 */
@ConfigurationProperties("httpclient")
public class HttpClientProperties implements Serializable {
    private static final long serialVersionUID = 1949516118786655126L;
    private Integer maxTotal;
    private Integer defaultMaxPerRoute;
    private Integer connectTimeout;
    private Integer connectionRequestTimeout;
    private Integer socketTimeout;
    private Integer validateAfterInactivity;

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(Integer defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getValidateAfterInactivity() {
        return validateAfterInactivity;
    }

    public void setValidateAfterInactivity(Integer validateAfterInactivity) {
        this.validateAfterInactivity = validateAfterInactivity;
    }
}