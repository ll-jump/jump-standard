package com.jump.standard.kafka.config;

import com.jump.standard.kafka.consumer.KafkaConsumer;
import com.jump.standard.kafka.consumer.SimpleKafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 〈kafka bean配置〉
 *
 * @author LiLin
 * @date 2020/7/17 0017
 */
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.topic.total}")
    private String topic;
    @Bean
    public KafkaConsumer kafkaConsumer(){
        return new SimpleKafkaConsumer(topic);
    }
}