package com.jump.standard.commons.sensitive.annotations;

import com.jump.standard.commons.sensitive.enums.SensitiveRulesEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 〈脱敏注解〉
 *
 * @author LiLin
 * @date 2020/5/9 0009
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Sensitive {
    SensitiveRulesEnum format() default SensitiveRulesEnum.DESENSITIZED_FULLY;
    boolean ignore() default false;
}
