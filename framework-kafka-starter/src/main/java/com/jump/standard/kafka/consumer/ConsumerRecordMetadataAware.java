package com.jump.standard.kafka.consumer;

/**
 * 〈kafka消息元数据获取接口〉
 *
 * @author LiLin
 * @date 2020/7/17 0017
 */
public interface ConsumerRecordMetadataAware {
    void setConsumerRecordMetadata(ConsumerRecordMetadata consumerRecordMetadata);
}
