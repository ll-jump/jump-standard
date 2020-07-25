package com.jump.standard.commons.sensitive.fieldhandles;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jump.standard.commons.sensitive.annotations.SensitiveJson;
import com.jump.standard.commons.sensitive.enums.SensitiveRulesEnum;
import com.jump.standard.commons.sensitive.utils.SensitiveProcessUtils;

import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 〈json字段脱敏处理类〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
public class JsonFieldHandler implements FieldHandler {
    @Override
    public boolean ignore(Field field) {
        SensitiveJson sensitiveJson = (SensitiveJson) field.getAnnotation(SensitiveJson.class);
        if (sensitiveJson == null) {
            return false;
        } else {
            return sensitiveJson.ignore();
        }
    }

    @Override
    public Object getValue(Field field, Object fieldValue) {
        SensitiveJson sensitiveJson = field.getAnnotation(SensitiveJson.class);
        if (sensitiveJson != null && fieldValue != null) {
            String formatPattern = sensitiveJson.format();
            JSONObject formatPatternJson = null;
            formatPatternJson = JSONObject.parseObject(formatPattern);
            if (MapUtils.isEmpty(formatPatternJson)) {
                return fieldValue;
            } else {
                String jsonVal = "";
                if (fieldValue instanceof String) {
                    jsonVal = String.valueOf(fieldValue);
                } else {
                    jsonVal = JSON.toJSONString(fieldValue, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat});
                }

                Map<String, SensitiveRulesEnum> fieldsRuleMap = new HashMap();
                Iterator var8 = formatPatternJson.entrySet().iterator();

                while (var8.hasNext()) {
                    Map.Entry<String, Object> entry = (Map.Entry) var8.next();
                    String key = entry.getKey();
                    String singleFormatPattern = String.valueOf(formatPatternJson.get(key));
                    SensitiveRulesEnum sensitiveRulesEnum = SensitiveRulesEnum.getSensitiveRule(singleFormatPattern);
                    if (sensitiveRulesEnum != null) {
                        fieldsRuleMap.put(key, sensitiveRulesEnum);
                    }
                }

                return SensitiveProcessUtils.jsonShield(jsonVal, fieldsRuleMap);
            }
        } else {
            return fieldValue;
        }
    }
}