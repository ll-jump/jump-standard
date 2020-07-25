package com.jump.standard.commons.sensitive;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jump.standard.commons.sensitive.annotations.Sensitive;
import com.jump.standard.commons.sensitive.annotations.SensitiveJson;
import com.jump.standard.commons.sensitive.constants.SensitiveConstants;
import com.jump.standard.commons.sensitive.enums.SensitiveFieldTypeEnum;
import com.jump.standard.commons.sensitive.fieldhandles.FieldHandler;
import com.jump.standard.commons.sensitive.fieldhandles.FieldHandlerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 〈〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
public class SensitiveStringBuilder {
    private static final Logger log = LoggerFactory.getLogger(SensitiveStringBuilder.class);

    public SensitiveStringBuilder() {
    }

    private static SensitiveFieldTypeEnum getFiledType(Field field) {
        if (field.isAnnotationPresent(Sensitive.class)) {
            return SensitiveFieldTypeEnum.GENERAL;
        } else {
            return field.isAnnotationPresent(SensitiveJson.class) ? SensitiveFieldTypeEnum.JSON : SensitiveFieldTypeEnum.NONE;
        }
    }

    public static String reflectionToString(Object object) {
        try {
            ToStringBuilder toStringBuilder = new ReflectionToStringBuilder(object, JToStringStyle.JSON_STYLE) {
                @Override
                protected boolean accept(Field field) {
                    try {
                        Object fieldValue = super.getValue(field);
                        if (fieldValue == null) {
                            return false;
                        } else if (fieldValue instanceof String && StringUtils.isBlank(String.valueOf(fieldValue))) {
                            return false;
                        } else {
                            SensitiveFieldTypeEnum fieldType = SensitiveStringBuilder.getFiledType(field);
                            FieldHandler fieldHandler = FieldHandlerFactory.getFieldHandler(fieldType);
                            if (fieldHandler == null) {
                                return super.accept(field);
                            } else {
                                return fieldHandler.ignore(field) ? false : super.accept(field);
                            }
                        }
                    } catch (Exception var5) {
                        SensitiveStringBuilder.log.info("日志脱敏SensitiveStringBuilder#accept异常", var5);
                        return false;
                    }
                }

                @Override
                protected Object getValue(Field field) {
                    Object fieldValue = null;

                    try {
                        fieldValue = super.getValue(field);
                        SensitiveFieldTypeEnum fieldType = SensitiveStringBuilder.getFiledType(field);
                        FieldHandler fieldHandler = FieldHandlerFactory.getFieldHandler(fieldType);
                        if (fieldHandler == null) {
                            if (!(fieldValue instanceof String) && !field.getType().isEnum()) {
                                if (fieldValue instanceof Date) {
                                    return ((SimpleDateFormat) SensitiveConstants.DATE_FORMAT.get()).format(fieldValue);
                                } else {
                                    try {
                                        return !fieldValue.toString().startsWith("{") && !fieldValue.toString().startsWith(String.valueOf('[')) ? JSON.toJSONString(fieldValue, new SerializerFeature[]{SerializerFeature.WriteDateUseDateFormat}) : fieldValue.toString();
                                    } catch (Exception var6) {
                                        SensitiveStringBuilder.log.info("日志脱敏JSON.toJSONString异常", var6);
                                        return fieldValue;
                                    }
                                }
                            } else {
                                return fieldValue;
                            }
                        } else {
                            return fieldHandler.getValue(field, fieldValue);
                        }
                    } catch (Exception var7) {
                        SensitiveStringBuilder.log.info("日志脱敏SensitiveStringBuilder#getValue异常", var7);
                        return fieldValue;
                    }
                }
            };
            return toStringBuilder.toString();
        } catch (Exception var2) {
            log.info("日志脱敏异常", var2);
            return "";
        }
    }
}