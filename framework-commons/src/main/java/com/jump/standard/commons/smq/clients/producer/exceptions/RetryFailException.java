package com.jump.standard.commons.smq.clients.producer.exceptions;

/**
 * @author LiLin
 * @desc 重试失败异常
 * @create 2021-06-07 20:46
 **/
public class RetryFailException extends SmqException {
    private static final long serialVersionUID = 2846077574225821517L;

    public RetryFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryFailException(String message) {
        super(message);
    }

    public RetryFailException(Throwable cause) {
        super(cause);
    }

    public RetryFailException() {
    }
}
