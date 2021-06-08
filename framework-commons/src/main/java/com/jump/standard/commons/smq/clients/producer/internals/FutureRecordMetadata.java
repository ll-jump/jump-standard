package com.jump.standard.commons.smq.clients.producer.internals;

import com.jump.standard.commons.smq.clients.producer.bo.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author LiLin
 * @desc 请求结果feature
 * @create 2021-06-07 11:36
 **/
public class FutureRecordMetadata implements Future<RecordMetadata> {
    /**
     * 消息组
     */
    private final String group;
    /**
     * 消息唯一键
     */
    private final String key;
    /**
     * 消息时间戳
     */
    private final long timestamp;
    /**
     * 请求结果
     */
    private final ProduceRequestResult result;

    public FutureRecordMetadata(String group, String key,long timestamp, ProduceRequestResult result) {
        this.group = group;
        this.key = key;
        this.timestamp = timestamp;
        this.result = result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }


    @Override
    public boolean isDone() {
        return this.result.completed();
    }


    @Override
    public RecordMetadata get() throws InterruptedException, ExecutionException {
        this.result.await();
        return valueOrError();
    }

    @Override
    public RecordMetadata get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean occurred = this.result.await(timeout, unit);
        if (!occurred) {
            throw new TimeoutException("Timeout after waiting fro " + TimeUnit.MILLISECONDS.convert(timeout, unit) + "ms.");
        }

        return valueOrError();
    }

    /**
     * 获取最终结果
     *
     * @return
     */
    private RecordMetadata valueOrError() throws ExecutionException {
        if (this.result.getError() != null) {
            throw new ExecutionException(this.result.getError());
        }

        return new RecordMetadata(this.group, this.key, this.timestamp);
    }
}
