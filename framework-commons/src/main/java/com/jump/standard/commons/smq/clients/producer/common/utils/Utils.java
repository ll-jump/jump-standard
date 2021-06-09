package com.jump.standard.commons.smq.clients.producer.common.utils;

/**
 * @author LiLin
 * @desc 工具类
 * @create 2021-06-07 17:03
 **/
public class Utils {
    /**
     * 获取系统的换行符
     */
    public static final String NL = System.getProperty("line.separator");

    /**
     * Get the ClassLoader which loaded smq.
     */
    public static ClassLoader getSmqClassLoader() {
        return Utils.class.getClassLoader();
    }

    /**
     * Get the Context ClassLoader on this thread or, if not present, the ClassLoader that
     * loaded smq.
     * <p>
     * This should be used whenever passing a ClassLoader to Class.forName
     */
    public static ClassLoader getContextOrSmqClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            return getSmqClassLoader();
        } else {
            return cl;
        }
    }
}
