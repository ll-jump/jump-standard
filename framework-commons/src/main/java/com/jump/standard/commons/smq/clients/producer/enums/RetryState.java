package com.jump.standard.commons.smq.clients.producer.enums;

/**
 * 重试状态
 */
public enum RetryState {
    CAN_RETRY(1, "可以重试"),
    NO_INTERVAL(2, "不可重试，时间间隔未到"),
    NO_OVER_LIMIT(3, "不可重试，重试次数超过最大重试次数");
    private int code;
    private String message;

    RetryState(int code, String message) {
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
