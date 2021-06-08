package com.jump.standard.commons.smq.clients.producer.exceptions;

/**
 * @author LiLin
 * @desc 可重试异常 在RecordDeal接口中抛出该异常，消息会重试
 * @create 2021-06-07 20:46
 **/
public class RetrieableException extends SmqException{
    private static final long serialVersionUID = 2684839898231500601L;
    public RetrieableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrieableException(String message) {
        super(message);
    }

    public RetrieableException(Throwable cause) {
        super(cause);
    }

    public RetrieableException() {
    }
}
