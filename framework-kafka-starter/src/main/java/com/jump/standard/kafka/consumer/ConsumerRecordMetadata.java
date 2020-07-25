package com.jump.standard.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.record.TimestampType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 〈kafka消息元数据〉
 *
 * @author LiLin
 * @date 2020/7/17 0017
 */
public class ConsumerRecordMetadata {
    private int partition;
    private long offset;
    private long timestamp;
    private TimestampType timestampType;
    private Map<String, byte[]> headers = new HashMap();

    public ConsumerRecordMetadata(ConsumerRecord consumerRecord){
        this.partition = consumerRecord.partition();
        this.offset = consumerRecord.offset();
        this.timestamp = consumerRecord.timestamp();
        this.timestampType = consumerRecord.timestampType();
        this.putAllHeaders(consumerRecord.headers());
    }

    private void putAllHeaders(Headers headers) {
        Iterator it = headers.iterator();

        while(it.hasNext()) {
            Header header = (Header)it.next();
            this.headers.put(header.key(), header.value());
        }
    }

    public int partition() {
        return this.partition;
    }

    public long offset() {
        return this.offset;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public TimestampType timestampType() {
        return this.timestampType;
    }

    public Map<String, byte[]> headers() {
        return this.headers;
    }

    public byte[] headerValue(String key) {
        return (byte[])this.headers.get(key);
    }
}