package com.jump.standard.kafka.consumer;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈kafka消费者监听器〉
 *
 * @author LiLin
 * @date 2020/7/17 0017
 */
public class KafkaConsumerListener implements AcknowledgingMessageListener<byte[], byte[]> {
    private static final ThreadLocal<ConsumerRecordMetadata> METADATA = new ThreadLocal<>();
    private final Map<String[], KafkaConsumerProcessor> processors = new ConcurrentHashMap<>(64);

    @Override
    public void onMessage(ConsumerRecord<byte[], byte[]> consumerRecord, Acknowledgment acknowledgment) {
        try {
            METADATA.set(new ConsumerRecordMetadata(consumerRecord));
            this.onMessage(consumerRecord);
        } finally {
            METADATA.remove();
        }

        acknowledgment.acknowledge();
    }

    @Override
    public void onMessage(ConsumerRecord<byte[], byte[]> data) {
        KafkaConsumerProcessor processor = this.findKafkaConsumerProcessor(data.topic());
        if (processor != null) {
            if (processor instanceof ConsumerRecordMetadataAware) {
                ((ConsumerRecordMetadataAware)processor).setConsumerRecordMetadata(METADATA.get());
            }
            processor.doProcess(data.topic(), data.key(), data.value());
        }
    }

    public void addKafkaConsumerProcessor(String[] topics, KafkaConsumerProcessor kafkaConsumerProcessor) {
        processors.put(topics, kafkaConsumerProcessor);
    }

    public KafkaConsumerProcessor getKafkaConsumerProcessor(String topic) {
        return findKafkaConsumerProcessor(topic);
    }

    /**
     * 根据主题获取对应的消费者处理器
     *
     * @param topic
     * @return
     */
    private KafkaConsumerProcessor findKafkaConsumerProcessor(String topic) {
        Iterator iterator = this.processors.entrySet().iterator();
        Map.Entry entry;
        do {
            if (!iterator.hasNext()) {
                return null;
            }
            entry = (Map.Entry)iterator.next();
        } while (!ArrayUtils.contains((Object[])entry.getKey(), topic));

        return (KafkaConsumerProcessor)entry.getValue();
    }

}