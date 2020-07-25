package com.jump.standard.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 〈kafka消费者处理器〉
 *
 * @author LiLin
 * @date 2020/7/17 0017
 */
public interface KafkaConsumerProcessor {
    /**
     * 消息消费处理
     * @param topic
     * @param key
     * @param value
     */
    void doProcess(String topic, byte[] key, byte[] value);

    /**
     * 消费是否过滤 false处理消息，true不处理消息
     * @param consumerRecord
     * @return
     */
    default boolean filter(ConsumerRecord<byte[],byte[]> consumerRecord){return false;}
}
