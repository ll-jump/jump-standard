package com.jump.standard.commons.smq.clients.producer.bo;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

/**
 * @author LiLin
 * @desc 消息记录
 * @create 2021-06-07 10:44
 **/
public final class ProducerRecord<V> {
    /**
     * 消息组
     */
    private final String group;
    /**
     * 消息唯一键
     */
    private final String key;
    /**
     * 值
     */
    private final V value;
    /**
     * 时间戳
     */
    private final Long timestamp;

    public ProducerRecord(String key, V value, Long timestamp) {
       this(null, key, value, timestamp);
    }

    public ProducerRecord(String group, String key, V value, Long timestamp) {
        this.group = group;
        if (StringUtils.isBlank(key)) {
            this.key = UUID.randomUUID().toString();
        } else {
            this.key = key;
        }
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        String group = this.group == null ? "null" : this.group;
        String key = this.key == null ? "null" : this.key;
        String value = this.value == null ? "null" : this.value.toString();
        String timestamp = this.timestamp == null ? "null" : this.timestamp.toString();
        return "ProducerRecord(group=" + group + ",key=" + key + ",value=" + value + ",timestamp=" + timestamp + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof ProducerRecord)) {
            return false;
        }

        ProducerRecord<?> that = (ProducerRecord<?>) o;
        if (!Objects.equals(key, that.key)) {
            return false;
        } else if (!Objects.equals(value, that.value)) {
            return false;
        } else if (!Objects.equals(timestamp, that.timestamp)) {
            return false;
        }

        return true;
    }

    public String group(){
        return this.group;
    }

    public String key() {
        return key;
    }

    public V value() {
        return value;
    }

    public Long timestamp() {
        return timestamp;
    }
}
