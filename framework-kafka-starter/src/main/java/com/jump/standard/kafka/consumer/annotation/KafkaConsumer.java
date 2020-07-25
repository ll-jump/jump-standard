package com.jump.standard.kafka.consumer.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 消费者注解
 * @author LiLin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Service
public @interface KafkaConsumer {
    //使用配置表达式配置 如${jump.topic}
    String topic();
}
