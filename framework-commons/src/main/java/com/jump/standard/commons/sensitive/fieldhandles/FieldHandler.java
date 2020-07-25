package com.jump.standard.commons.sensitive.fieldhandles;

import java.lang.reflect.Field;

/**
 * 〈字段脱敏操作接口〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
public interface FieldHandler {
    boolean ignore(Field var1);

    Object getValue(Field var1, Object var2);
}
