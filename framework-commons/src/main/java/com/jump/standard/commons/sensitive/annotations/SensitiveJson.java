package com.jump.standard.commons.sensitive.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 〈json脱敏注解〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SensitiveJson {
    String format() default "";

    boolean ignore() default false;
}
