package com.jump.standard.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 〈kafka生产者〉
 *
 * @author LiLin
 * @date 2020/7/13 0013
 */
@Service
public class KafkaProducer {
    /**
     * 默认超时时间
     */
    private static final long TIME_OUT_DEFAULT = 5000;
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<byte[], byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 异步推送消息到kafka
     *
     * @param topic    主题
     * @param key
     * @param value    值 可以为空
     * @param callback
     */
    public void asyncSend(String topic, byte[] key, @Nullable byte[] value,
        ListenableFutureCallback<SendResult<byte[], byte[]>> callback) {
        ListenableFuture<SendResult<byte[], byte[]>> listenableFuture = kafkaTemplate.send(topic, key, value);
        listenableFuture.addCallback(callback);
        kafkaTemplate.flush();
    }

    public void asyncSend(String topic, String key, @Nullable byte[] value,
        ListenableFutureCallback<SendResult<byte[], byte[]>> callback) {
        asyncSend(topic, key.getBytes(StandardCharsets.UTF_8), value, callback);
    }

    public void asyncSend(String topic, String key, @Nullable byte[] value,
        ListenableFutureCallback<SendResult<byte[], byte[]>> callback, Charset charset) {
        asyncSend(topic, key.getBytes(charset), value, callback);
    }

    public void asyncSend(String topic, String key, String value,
        ListenableFutureCallback<SendResult<byte[], byte[]>> callback, Charset charset) {
        asyncSend(topic, key.getBytes(charset), value.getBytes(charset), callback);
    }

    public void asyncSend(String topic, String key, String value,
        ListenableFutureCallback<SendResult<byte[], byte[]>> callback) {
        asyncSend(topic, key, value, callback, StandardCharsets.UTF_8);
    }

    /**
     * 同步推送消息到kafka
     *
     * @param topic
     * @param key
     * @param value
     * @param timeOut 超时时间 单位毫秒
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public ProducerResult syncSend(String topic, byte[] key, byte[] value, long timeOut)
        throws InterruptedException, ExecutionException, TimeoutException {
        ListenableFuture<SendResult<byte[], byte[]>> listenableFuture = kafkaTemplate.send(topic, key, value);
        kafkaTemplate.flush();
        try {
            return new ProducerResult(listenableFuture.get(timeOut, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        } catch (TimeoutException e) {
            throw e;
        }
    }

    public ProducerResult syncSend(String topic, byte[] key, byte[] value)
        throws InterruptedException, ExecutionException, TimeoutException {
        return syncSend(topic, key, value, TIME_OUT_DEFAULT);
    }

    public ProducerResult syncSend(String topic, String key, byte[] value, Charset charset)
        throws InterruptedException, ExecutionException, TimeoutException {
        return syncSend(topic, key.getBytes(charset), value);
    }

    public ProducerResult syncSend(String topic, String key, byte[] value)
        throws InterruptedException, ExecutionException, TimeoutException {
        return syncSend(topic, key, value, StandardCharsets.UTF_8);
    }

    public ProducerResult syncSend(String topic, String key, String value, Charset charset)
        throws InterruptedException, ExecutionException, TimeoutException {
        return syncSend(topic, key.getBytes(charset), value.getBytes(charset));
    }

    public ProducerResult syncSend(String topic, String key, String value)
        throws InterruptedException, ExecutionException, TimeoutException {
        return syncSend(topic, key, value, StandardCharsets.UTF_8);
    }
}