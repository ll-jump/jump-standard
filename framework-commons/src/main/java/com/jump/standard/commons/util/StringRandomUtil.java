package com.jump.standard.commons.util;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 随机字符串生成工具类
 * @author LiLin
 * @date 2020/06/29
 * @version 1.0.0
 */
public class StringRandomUtil {
    
    private static Map<String, String> BASE_TYPE = new HashMap<>();
    
    static {
        BASE_TYPE.put("mix", "abcdefghijklmnopqrstuvwxyz0123456789");
        BASE_TYPE.put("num", "0123456789");
        BASE_TYPE.put("char", "abcdefghijklmnopqrstuvwxyz");
    }
    
    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String NUMBERCHAR = "0123456789";

    /**
     * 返回一个定长的随机数字(只包含数字)
     * 
     * @param length 随机数字长度
     * @return 随机数字
     */
    public static String generateNum(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(NUMBERCHAR.charAt(random.nextInt(NUMBERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     * 
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯字母字符串(只包含大小写字母)
     * 
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateMixString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(LETTERCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 返回一个定长的随机纯大写字母字符串(只包含大小写字母)
     * 
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateLowerString(int length) {
        return generateMixString(length).toLowerCase();
    }

    /**
     * 返回一个定长的随机纯小写字母字符串(只包含大小写字母)
     * 
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateUpperString(int length) {
        return generateMixString(length).toUpperCase();
    }

    /**
     * 生成一个定长的纯0字符串
     * 
     * @param length 字符串长度
     * @return 纯0字符串
     */
    public static String generateZeroString(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append('0');
        }
        return sb.toString();
    }

    /**
     * 根据数字生成一个定长的字符串，长度不够前面补0
     * 
     * @param num       数字
     * @param fixdlenth 字符串长度
     * @return 定长的字符串
     */
    public static String toFixdLengthString(long num, int fixdlenth) {
        StringBuffer sb = new StringBuffer();
        String strNum = String.valueOf(num);
        if (fixdlenth - strNum.length() >= 0) {
            sb.append(generateZeroString(fixdlenth - strNum.length()));
        } else {
            throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth + "的字符串发生异常！");
        }
        sb.append(strNum);
        return sb.toString();
    }

    /**
     * 返回长度为【strLength】的随机数，在前面补0
     * 
     * @param strLength
     * @return
     */
    public static String getFixLenthString(int strLength) {
        Random rm = new Random();
        // 获得随机数
        Double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        return String.valueOf(pross.longValue());
    }

    /**
     * 获取UUID
     * 
     * @return
     */
    public static String uuId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    public static String uuIdUpperCase() {
        return uuId().toUpperCase();
    }

    public static final String STR62 = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 得到短码UUID，对于日志流水够用，占空间小
     * 
     * @return
     */
    public static String uuidShort() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            shortBuffer.append(STR62.charAt((int) (Long.parseLong(str, 16) % 0x3E)));
        }
        return shortBuffer.toString();
    }

    /**
     * 生成随机数字和字母
     * 
     * @param length
     * @return
     */
    public static String getStringRandom(int length) {
        StringBuilder val = new StringBuilder();
        SecureRandom random = new SecureRandom();
        // 参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char)(random.nextInt(26) + temp));
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val.append(random.nextInt(10));
            }
        }
        return val.toString();
    }

    /**
     * 生成随机数字
     *
     * @param length
     * @return
     */
    public static String getNumRandom(int length) {
        StringBuilder val = new StringBuilder();
        SecureRandom random = new SecureRandom();
        // 参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            val.append(random.nextInt(10));
        }
        return val.toString();
    }

    public static String getRandomString(int length, String type) {
        String base =  BASE_TYPE.containsKey("type") ? BASE_TYPE.get(type) : "mix";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * 分 转 元
     * @param fen
     * @return
     */
    public static String fenToYuan(Integer fen) {
        BigDecimal fenBd = new BigDecimal(fen);
        BigDecimal yuanBd = fenBd.divide(new BigDecimal(100));
        DecimalFormat df1 = new DecimalFormat("0.00");
        return df1.format(yuanBd);
    }
    public static void main(String[] args) {
        System.out.println(StringRandomUtil.getRandomString(16, "num"));
    }

}
