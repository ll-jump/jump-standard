package com.jump.standard.commons.sensitive.utils;

import com.alibaba.fastjson.JSON;
import com.jump.standard.commons.sensitive.enums.SensitiveRulesEnum;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 〈脱敏工具类〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
public class SensitiveProcessUtils {
    private static final Logger log = LoggerFactory.getLogger(SensitiveProcessUtils.class);

    public SensitiveProcessUtils() {
    }

    public static String shield(SensitiveRulesEnum sensitiveFormat, String info) {
        if (!StringUtils.isBlank(info) && sensitiveFormat != null) {
            String sensitiveInfo = "N.A.";

            try {
                SensitiveRulesEnum.RuleEnum rule = sensitiveFormat.getRule();
                if (rule.isShow()) {
                    switch (rule) {
                        case RULE_EMAIL:
                            int emailIndex = info.indexOf("@");
                            if (emailIndex == -1) {
                                return info;
                            }

                            sensitiveInfo = sensitiveStr(rule, info.substring(0, emailIndex)) + info.substring(emailIndex);
                            break;
                        case RULE_HASH:
                            sensitiveInfo = DigestUtils.md5Hex(info);
                            break;
                        default:
                            sensitiveInfo = sensitiveStr(rule, info);
                    }
                }
            } catch (Exception var5) {
                log.info("shield catch防异常", var5);
            }

            return sensitiveInfo;
        } else {
            return info;
        }
    }

    private static String sensitiveStr(SensitiveRulesEnum.RuleEnum sensitiveFormat, String info) {
        StringBuffer sb = new StringBuffer();
        int sensitiveIndex = sensitiveFormat.getBeforeIndex() + sensitiveFormat.getAfterIndex();
        if (StringUtils.isBlank(info)) {
            return info;
        } else {
            if (info.length() <= sensitiveIndex) {
                sensitiveFormat = SensitiveRulesEnum.RuleEnum.RULE_DESENSITIZED_1_;
                sensitiveIndex = sensitiveFormat.getBeforeIndex() + sensitiveFormat.getAfterIndex();
            }

            sb.append(StringUtils.substring(info, 0, sensitiveFormat.getBeforeIndex()));

            for (int i = 0; i < sensitiveFormat.getPaddingStar(); ++i) {
                sb.append("*");
            }

            sb.append(info.substring(info.length() - sensitiveFormat.getAfterIndex(), info.length()));
            return sb.toString();
        }
    }

    public static String jsonShield(String jsonVal, Map<String, SensitiveRulesEnum> fields) {
        try {
            if (MapUtils.isNotEmpty(fields) && StringUtils.isNotBlank(jsonVal)) {
                if (jsonVal.charAt(0) == '[') {
                    jsonVal = JSON.toJSONString(JSON.parseArray(jsonVal));
                } else {
                    jsonVal = JSON.toJSONString(JSON.parseObject(jsonVal));
                }

                Iterator var2 = fields.entrySet().iterator();

                while (var2.hasNext()) {
                    Map.Entry<String, SensitiveRulesEnum> entry = (Map.Entry) var2.next();
                    String key = (String) entry.getKey();
                    String fieldRegex = MessageFormat.format("\\\\*\"({0})\\\\*\":\\\\*\"([^\"\\\\]+)\\\\*\"", key);

                    StringBuilder sb;
                    for (Matcher matcher = Pattern.compile(fieldRegex).matcher(jsonVal); matcher.find(); sb = null) {
                        sb = new StringBuilder();
                        sb.append("\"").append(matcher.group(1)).append("\":\"").append(shield((SensitiveRulesEnum) fields.get(key), matcher.group(2))).append("\"");
                        jsonVal = StringUtils.replace(jsonVal, matcher.group(0), sb.toString());
                    }
                }
            }

            return jsonVal;
        } catch (Exception var8) {
            log.info("[jsonShield]JSON字符串脱敏异常,注意JSON格式", var8);
            return jsonVal;
        }
    }

    public static String dataShield(String srcData, Map<String, SensitiveRulesEnum> fields) {
        try {
            if (MapUtils.isNotEmpty(fields) && StringUtils.isNotBlank(srcData)) {
                Iterator var2 = fields.keySet().iterator();

                while (var2.hasNext()) {
                    String fieldName = (String) var2.next();
                    Matcher matcher = Pattern.compile("([^,\\s\\d-\\[\\]&?{}][\\s*\\w]+?)=([^,\"\\*\\[\\]{}<]\\s*[[^\\x00-\\xff]_a-zA-Z0-9]*)").matcher(srcData);

                    String tmpstr;
                    StringBuilder sb;
                    while (matcher.find()) {
                        tmpstr = matcher.group(1).trim();
                        if (tmpstr.startsWith("(")) {
                            tmpstr = tmpstr.substring(1);
                        }

                        if (StringUtils.equals(tmpstr, fieldName)) {
                            sb = new StringBuilder();
                            sb.append(matcher.group(1)).append("=").append(shield((SensitiveRulesEnum) fields.get(fieldName), matcher.group(2)));
                            srcData = StringUtils.replace(srcData, matcher.group(0), sb.toString());
                            sb = null;
                        }
                    }

                    tmpstr = MessageFormat.format("\\\\*\"({0})\\\\*\":\\\\*\"([^\"\\\\]+)\\\\*\"", fieldName);

                    for (matcher = Pattern.compile(tmpstr).matcher(srcData); matcher.find(); sb = null) {
                        sb = new StringBuilder();
                        sb.append("\"").append(matcher.group(1)).append("\":\"").append(shield((SensitiveRulesEnum) fields.get(fieldName), matcher.group(2))).append("\"");
                        srcData = StringUtils.replace(srcData, matcher.group(0), sb.toString());
                    }
                }
            }

            return srcData;
        } catch (Exception var7) {
            return srcData;
        }
    }
}