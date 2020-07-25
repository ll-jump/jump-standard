package com.jump.standard.kafka.consumer;

/**
 * 〈kafka消费者默认实现类〉
 *
 * @author LiLin
 * @date 2020/7/17 0017
 */
public class SimpleKafkaConsumer extends KafkaConsumer {
    private String topic;
    public SimpleKafkaConsumer(String topic){
        this.topic = topic;
    }
    @Override
    protected String topic() {
        return this.topic;
    }
}