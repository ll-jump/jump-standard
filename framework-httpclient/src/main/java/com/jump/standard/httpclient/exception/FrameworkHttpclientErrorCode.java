package com.jump.standard.httpclient.exception;

/**
 * 异常编码
 * @author LiLin
 */
public enum FrameworkHttpclientErrorCode {
    E001001("001001","请求响应超时");
    FrameworkHttpclientErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }
    private String code;
    private String message;
    public String getCode(){
        return this.code;
    }
    public String getMessage(){
        return this.message;
    }
}
