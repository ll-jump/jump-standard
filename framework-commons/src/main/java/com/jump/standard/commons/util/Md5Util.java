package com.jump.standard.commons.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;

/**
 * 〈MD5工具类〉
 *
 * @author LiLin
 * @create 2019/6/5 0005
 */
public class Md5Util {
    public static String encrypt(String input){
        return md5Hex(input);
    }
    /**
     * 加盐MD5
     * @param input
     * @param salt
     * @return
     */
    public static String encryptSalt(String input, String salt) {
        StringBuilder sb = new StringBuilder(input);
        sb.reverse();
        input = sb.toString();
        if (StringUtils.isNotEmpty(salt)) {
            salt = initSalt(salt);
        } else {
            //默认盐
            salt = "2020DumJuMpItami";
        }
        input = md5Hex(input + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = input.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = input.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 初始化盐
     * @param salt
     * @return
     */
    private static String initSalt(String salt) {
        String result = salt;

        while (result.length() < 16) {
            result = result + "0";
        }
        return result;
    }

    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    private static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String encryptStr = encryptSalt("123","123456");
        System.out.println("encryptStr:" + encryptStr);
    }
}
