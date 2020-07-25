package com.jump.standard.commons.sensitive.constants;

import java.text.SimpleDateFormat;

/**
 * 〈常量类〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
public class SensitiveConstants {
    public static final String IDENTIFIER_A_COMMA = ",";
    public static final String IDENTIFIER_STAR = "*";
    public static final String IDENTIFIER_AT = "@";
    public static final String DOUBLE_QUOTATION = "\"";
    public static final String MARK_JSON = "\":\"";
    public static final char LEFT_BRACKET = '[';
    public static final String LEFT_SMALLBRACKET = "(";
    public static final char RIGHT_BRACKET = ']';
    public static final String LEFT_BRACE = "{";
    public static final String MARK_EQUAL = "=";
    public static final String REGEX_JSON = "\\\\*\"({0})\\\\*\":\\\\*\"([^\"\\\\]+)\\\\*\"";
    public static final String REGEX_EQUAL = "([^,\\s\\d-\\[\\]&?{}][\\s*\\w]+?)=([^,\"\\*\\[\\]{}<]\\s*[[^\\x00-\\xff]_a-zA-Z0-9]*)";
    public static final String REGEX_LIST = "\\\\*\"([^\"\\\\]+)\\\\*\"";
    public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
}