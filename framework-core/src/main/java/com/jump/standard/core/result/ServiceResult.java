package com.jump.standard.core.result;


import java.io.Serializable;

/**
 * 〈统一返回结果〉
 *
 * @author LiLin
 * @create 2019/9/20 0020
 */
public class ServiceResult<T> implements Serializable {
    private static final long serialVersionUID = 1677745636837192505L;
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static ServiceResult success(Object data) {
        return success(data, "000000", "执行成功");
    }

    public static ServiceResult success(Object data, String code, String message) {
        ServiceResult result = new ServiceResult();
        result.setSuccess(true);
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static ServiceResult fail(String code, String message) {
        ServiceResult result = new ServiceResult();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}