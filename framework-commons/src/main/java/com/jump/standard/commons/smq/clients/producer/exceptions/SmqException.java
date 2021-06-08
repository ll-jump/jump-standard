package com.jump.standard.commons.smq.clients.producer.exceptions;

/**
 * @author LiLin
 * @desc smq异常基础类
 * @create 2021-06-07 14:56
 **/
public class SmqException extends RuntimeException{
    private static final long serialVersionUID = -9118049966553927827L;
    public SmqException(String message, Throwable cause){
        super(message, cause);
    }

    public SmqException(String message){
        super(message);
    }

    public SmqException(Throwable cause){
        super(cause);
    }

    public SmqException(){
        super();
    }
}
