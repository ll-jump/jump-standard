package com.jump.standard.httpclient.service.impl;

import com.google.gson.Gson;

import com.jump.standard.commons.sensitive.enums.SensitiveRulesEnum;
import com.jump.standard.commons.sensitive.utils.SensitiveProcessUtils;
import com.jump.standard.httpclient.exception.FrameworkHttpclientErrorCode;
import com.jump.standard.httpclient.exception.FrameworkHttpclientException;
import com.jump.standard.httpclient.service.HttpClientService;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

/**
 * HttpClient 实现类
 * @author LiLin
 */
public class HttpClientServiceImpl implements HttpClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientService.class);

    @Autowired
    private CloseableHttpClient closeableHttpClient;

    private Gson gson = new Gson();

    @Override
    public String postJson(String url, Map<String, Object> param) {
        return postJson(url, param, null);
    }

    @Override
    public String postJson(String url, Map<String, Object> param, Map<String, SensitiveRulesEnum> immutmap) {
        String json = gson.toJson(param);
        LOGGER.info("url【{}】,param【{}】", url, SensitiveProcessUtils.dataShield(json, immutmap));
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(json, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Connection", "close");
        CloseableHttpResponse resp = null;
        try {
            resp = closeableHttpClient.execute(httpPost);
            int statusCode = resp.getStatusLine().getStatusCode();
            LOGGER.info("statusCode【{}】", statusCode);
            if (statusCode == 200) {
                String res = EntityUtils.toString(resp.getEntity(), "UTF-8");
                LOGGER.info("res【{}】", SensitiveProcessUtils.dataShield(res, immutmap));
                return res;
            } else {
                throw new FrameworkHttpclientException("hcs01", "statusCode error:" + statusCode);
            }
        } catch (Exception e) {
            LOGGER.error("httpclient post json request error【{}】", e);
            throw new FrameworkHttpclientException(FrameworkHttpclientErrorCode.E001001);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException e) {
                    LOGGER.error("httpclient post json response close error【{}】", e);
                }
            }
        }
    }

    @Override
    public String postForm(String requestStr, String requestUrl) {
        return postForm(requestStr, requestUrl, null);
    }

    @Override
    public String postForm(String requestStr, String requestUrl, Map<String, SensitiveRulesEnum> immutmap) {
        LOGGER.info("url【{}】,param【{}】", requestUrl, SensitiveProcessUtils.dataShield(requestStr, immutmap));
        HttpPost httpPost = new HttpPost(requestUrl);
        StringEntity entity = new StringEntity(requestStr, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
        httpPost.setEntity(entity);
        httpPost.setHeader("Connection", "close");
        CloseableHttpResponse resp = null;
        try {
            resp = closeableHttpClient.execute(httpPost);
            int statusCode = resp.getStatusLine().getStatusCode();
            LOGGER.info("statusCode【{}】", statusCode);
            if (statusCode == 200) {
                String res = EntityUtils.toString(resp.getEntity(), "UTF-8");
                LOGGER.info("res【{}】", SensitiveProcessUtils.dataShield(res, immutmap));
                return res;
            } else {
                throw new FrameworkHttpclientException("hcs01", "statusCode error:" + statusCode);
            }
        } catch (SocketTimeoutException e) {
            LOGGER.error("请求响应超时.", e);
            throw new FrameworkHttpclientException(FrameworkHttpclientErrorCode.E001001);
        } catch (Exception e) {
            LOGGER.error("httpclient post xml request error.", e);
            throw new FrameworkHttpclientException("hcs02", e.getMessage(), e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException e) {
                    LOGGER.error("httpclient post xnk response close error.", e);
                }
            }
        }
    }

}
