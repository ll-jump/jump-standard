package com.jump.standard.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.SendResult;

/**
 * 〈生产者推送消息result〉
 *
 * @author LiLin
 * @date 2020/7/14 0014
 */
public class ProducerResult {
    private final ProducerRecord<byte[], byte[]> producerRecord;
    private final RecordMetadata recordMetadata;

    public ProducerResult(SendResult<byte[], byte[]> sendResult) {
        this.producerRecord = sendResult.getProducerRecord();
        this.recordMetadata = sendResult.getRecordMetadata();
    }

    public boolean hasOffset() {
        return this.recordMetadata.hasOffset();
    }

    public long offset() {
        return this.recordMetadata.offset();
    }

    public boolean hasTimestamp() {
        return this.recordMetadata.hasTimestamp();
    }

    public long timestamp() {
        return this.recordMetadata.timestamp();
    }

    public int serializedKeySize() {
        return this.recordMetadata.serializedKeySize();
    }

    public int serializedValueSize() {
        return this.recordMetadata.serializedValueSize();
    }

    public String topic() {
        return this.recordMetadata.topic();
    }

    public int partition() {
        return this.recordMetadata.partition();
    }

    public Headers headers() {
        return this.producerRecord.headers();
    }

    public byte[] key() {
        return (byte[])this.producerRecord.key();
    }

    public byte[] value() {
        return (byte[])this.producerRecord.value();
    }
}