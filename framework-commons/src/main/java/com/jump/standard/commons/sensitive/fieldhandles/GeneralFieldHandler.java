package com.jump.standard.commons.sensitive.fieldhandles;

import com.alibaba.fastjson.JSON;
import com.jump.standard.commons.sensitive.annotations.Sensitive;
import com.jump.standard.commons.sensitive.utils.SensitiveProcessUtils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 〈通用字段脱敏处理类〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
public class GeneralFieldHandler implements FieldHandler {
    @Override
    public boolean ignore(Field field) {
        Sensitive sensitive = field.getAnnotation(Sensitive.class);
        if (sensitive == null) {
            return false;
        } else {
            return sensitive.ignore();
        }
    }

    @Override
    public Object getValue(Field field, Object fieldValue) {
        Sensitive sensitive = (Sensitive) field.getAnnotation(Sensitive.class);
        if (sensitive != null && fieldValue != null) {
            String jsonStr;
            if (fieldValue instanceof List) {
                jsonStr = "";
                Iterator var5 = ((List) fieldValue).iterator();

                while (var5.hasNext()) {
                    Object object = var5.next();
                    if (object instanceof String) {

                        jsonStr = JSON.toJSONString(fieldValue);
                        break;
                    }
                }

                if (!StringUtils.equals(jsonStr, "")) {
                    for (Matcher matcher = Pattern.compile("\\\\*\"([^\"\\\\]+)\\\\*\"").matcher(jsonStr); matcher.find(); jsonStr = StringUtils.replace(jsonStr, matcher.group(1), SensitiveProcessUtils.shield(sensitive.format(), matcher.group(1)))) {
                    }

                    return jsonStr;
                }
            }

            jsonStr = String.valueOf(fieldValue);
            return SensitiveProcessUtils.shield(sensitive.format(), jsonStr);
        } else {
            return fieldValue;
        }
    }
}