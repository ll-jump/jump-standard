package com.jump.standard.httpclient.exception;

/**
 * 〈框架commons异常〉
 *
 * @author LiLin
 * @date 2020/6/29 0029
 */
public class FrameworkHttpclientException extends RuntimeException {
    /**
     * 错误编码
     */
    private String code;

    public String getCode() {
        return code;
    }

    public FrameworkHttpclientException(String code, String message){
        super(message);
        this.code =code;
    }
    public FrameworkHttpclientException(String code, String message, Throwable cause){
        super(message, cause);
        this.code =code;
    }

    public FrameworkHttpclientException(FrameworkHttpclientErrorCode errorCode){
        super(errorCode.getMessage());
        this.code =errorCode.getCode();
    }

    public FrameworkHttpclientException(FrameworkHttpclientErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(),cause);
        this.code =errorCode.getCode();
    }

    public FrameworkHttpclientException(FrameworkHttpclientErrorCode errorCode, String errorInfo){
        super(errorCode.getMessage() + "[" + errorInfo + "]");
        this.code =errorCode.getCode();
    }

    public FrameworkHttpclientException(FrameworkHttpclientErrorCode errorCode, String errorInfo, Throwable cause){
        super(errorCode.getMessage() + "[" + errorInfo + "]", cause);
        this.code =errorCode.getCode();
    }
}