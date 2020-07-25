package com.jump.standard.httpclient.service;

import com.jump.standard.commons.sensitive.enums.SensitiveRulesEnum;

import java.util.Map;

/**
 * 〈HttpClient接口〉
 *
 * @author LiLin
 * @date 2020/6/28 0028
 */

public interface HttpClientService {

    /**
     * post json + 日志打印脱敏规则
     * 
     * @param url
     * @param param
     * @param immutmap
     * @return
     */
    String postJson(String url, Map<String, Object> param, Map<String, SensitiveRulesEnum> immutmap);

    /**
     * post json
     * 
     * @param url
     * @param param
     * @return
     */
    String postJson(String url, Map<String, Object> param);

    /**
     * post form + 日志打印脱敏规则
     * 
     * @param requestStr
     * @param requestUrl
     * @return
     */
    String postForm(String requestStr, String requestUrl, Map<String, SensitiveRulesEnum> immutmap);

    /**
     * post form
     * 
     * @param requestStr
     * @param requestUrl
     * @return
     */
    String postForm(String requestStr, String requestUrl);
}