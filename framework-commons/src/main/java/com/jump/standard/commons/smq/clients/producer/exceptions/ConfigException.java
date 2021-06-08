package com.jump.standard.commons.smq.clients.producer.exceptions;

/**
 * @author LiLin
 * @desc 配置异常
 * @create 2021-06-07 14:59
 **/
public class ConfigException extends SmqException {
    private static final long serialVersionUID = -5712795701401452449L;

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String name, Object value) {
        this(name, value, null);
    }


    public ConfigException(String name, Object value, String message) {
        super("Invalid value " + value + " for configuration " + name + (message == null ? "" : ": " + message));
    }
}
