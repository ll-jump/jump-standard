package com.jump.standard.commons.smq.clients.producer.internals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author LiLin
 * @desc 请求结果处理组件
 * @create 2021-06-07 11:06
 **/
public final class ProduceRequestResult {
    private final CountDownLatch latch = new CountDownLatch(1);
    private volatile Exception error;
    /**
     * 处理完成时间戳
     */
    private long timestamp;

    public ProduceRequestResult() {
    }

    /**
     * 请求结束
     *
     * @param error
     */
    public void done(Exception error, long timestamp) {
        this.error = error;
        this.timestamp = timestamp;
        this.latch.countDown();
    }

    /**
     * 等待请求结束
     *
     * @throws InterruptedException
     */
    public void await() throws InterruptedException {
        this.latch.await();
    }

    /**
     * 等待请求结束，直到指定超时时间
     *
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.latch.await(timeout, unit);
    }

    public Exception getError() {
        return error;
    }

    /**
     * 获取请求是否结束
     *
     * @return
     */
    public boolean completed() {
        return this.latch.getCount() == 0L;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
