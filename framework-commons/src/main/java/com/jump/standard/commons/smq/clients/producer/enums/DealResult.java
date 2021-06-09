package com.jump.standard.commons.smq.clients.producer.enums;

/**
 * 处理结果枚举
 */
public enum DealResult {
    SUCCESS(0, "成功"),
    FAILED(1, "失败");
    private int code;
    private String message;

    DealResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
