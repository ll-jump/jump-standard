package com.jump.standard.commons.smq.clients.producer.bo;

/**
 * @author LiLin
 * @desc 消息元数据
 * @create 2021-06-07 11:13
 **/
public final class RecordMetadata {
    /**
     * 消息组
     */
    private final String group;
    /**
     * 消息唯一键
     */
    private final String key;
    /**
     * 消息处理结束时的时间戳
     */
    private final long timestamp;

    public RecordMetadata(String group, String key, long timestamp) {
        this.group = group;
        this.key = key;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RecordMetadata{" +
                "group='" + group + '\'' +
                ", key='" + key + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
