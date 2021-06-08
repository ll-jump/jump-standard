package com.jump.standard.commons.smq.clients.producer.common.config.types;

/**
 * @author LiLin
 * @desc 密码类
 * @create 2021-06-07 16:52
 **/
public class Password {
    public static final String HIDDEN = "[hidden]";
    private final String value;

    public Password(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Password)) {
            return false;
        }
        Password other = (Password) obj;
        return value.equals(other.value);
    }

    @Override
    public String toString() {
        return HIDDEN;
    }

    public String value() {
        return value;
    }
}
