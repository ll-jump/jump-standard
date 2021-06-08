package com.jump.standard.commons.smq.clients.producer.internals;

import com.jump.standard.commons.smq.clients.producer.CallBack;
import com.jump.standard.commons.smq.clients.producer.RecordDeal;
import com.jump.standard.commons.smq.clients.producer.bo.ProducerRecord;
import com.jump.standard.commons.smq.clients.producer.bo.RecordMetadata;
import com.jump.standard.commons.smq.clients.producer.enums.RetryState;
import com.jump.standard.commons.smq.clients.producer.exceptions.RetrieableException;
import com.jump.standard.commons.smq.clients.producer.exceptions.RetryFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LiLin
 * @desc 在队列中存储的消息对象
 * @create 2021-06-07 20:19
 **/
public class RecordInDeque {
    private static final Logger log = LoggerFactory.getLogger(RecordInDeque.class);

    /**
     * 消息
     */
    private final ProducerRecord producerRecord;
    /**
     * 消息处理接口
     */
    private final RecordDeal recordDeal;
    /**
     * 回调接口
     */
    private final CallBack callBack;
    /**
     * 最大重试次数
     */
    private final int retryMaxTimes;
    /**
     * 重试间隔 单位ms
     */
    private final long retryInterval;
    /**
     * 当前重试次数
     */
    public volatile int attempts = 0;
    /**
     * 消息创建时间戳
     */
    public final long createdMs;
    /**
     * 上次重试时间戳
     */
    public long lastAttemptMs;
    /**
     * 上次添加到队列中的时间戳
     */
    public long lastAppendTime;
    /**
     * 当前状态是否重试
     */
    private boolean retry;
    /**
     * 请求结果
     */
    public final ProduceRequestResult futureResult;

    public RecordInDeque(ProducerRecord producerRecord, RecordDeal recordDeal, CallBack callBack, int retryMaxTimes, long retryInterval, long now) {
        this.producerRecord = producerRecord;
        this.recordDeal = recordDeal;
        this.callBack = callBack;
        this.retryMaxTimes = retryMaxTimes;
        this.retryInterval = retryInterval;
        this.createdMs = now;
        this.lastAttemptMs = now;
        this.lastAppendTime = createdMs;
        this.retry = false;
        this.futureResult = new ProduceRequestResult();
    }

    /**
     * 处理消息
     *
     * @return true 需要重试；false不需要重试
     */
    public boolean deal() {
        try {
            this.recordDeal.deal(this.producerRecord);
            done(System.currentTimeMillis(), null);
            return false;
        } catch (RetrieableException e) {
            //如果重试次数大于最大重试次数，则处理结束，不再重试
            if (this.attempts >= this.retryMaxTimes) {
                done(System.currentTimeMillis(), new RetryFailException(String.format("重试{}次仍然失败，重试次数已达最大重试次数{}，不可再重试", this.attempts, this.retryMaxTimes)));
                return false;
            }
            //需要重试
            return true;
        } catch (Exception e) {
            done(System.currentTimeMillis(), e);
            return false;
        }
    }

    /**
     * 消息处理结束
     *
     * @param timestamp 处理结束时的时间戳
     * @param exception 异常
     */
    public void done(long timestamp, Exception exception) {
        if (this.callBack != null) {
            try {
                RecordMetadata recordMetadata = new RecordMetadata(this.producerRecord.group(), this.producerRecord.key(), timestamp);
                this.callBack.onCompletion(recordMetadata, exception);
            } catch (Exception e) {
                log.error("Error executing user-provided callback on message for producerRecord [group:{},key:{}]:", this.producerRecord.group(), this.producerRecord.key(), e);
            }
        }
        this.futureResult.done(exception, timestamp);
    }

    /**
     * 获取当前重试状态
     *
     * @param now 当前时间戳
     * @return 可以重试；重试间隔未达到，不可重试；已过最大重试次数，不可重试，且需要从重试队列中移除
     */
    public RetryState retryState(long now) {
        if (this.attempts >= this.retryMaxTimes) {
            //重试次数超过最大重试次数，不可重试
            return RetryState.NO_OVER_LIMIT;
        }

        if (now - this.lastAppendTime < this.retryInterval) {
            //重试间隔未达到，不可重试
            return RetryState.NO_INTERVAL;
        }

        return RetryState.CAN_RETRY;
    }

    /**
     * 获取剩余的重试间隔时间
     *
     * @param now
     * @return
     */
    public long leftRetryInterval(long now) {
        long leftRetryInterval = this.retryInterval - (now - this.lastAppendTime);
        return leftRetryInterval > 0 ? leftRetryInterval : 0;
    }

    /**
     * 消息重试
     *
     * @param now
     */
    public void retry(long now) {
        this.attempts++;
        this.lastAttemptMs = now;
    }

    /**
     * 是否处于重试状态
     *
     * @return
     */
    public boolean inRetry() {
        return this.retry;
    }

    /**
     * 设置为重试状态
     */
    public void setRetry(long now) {
        this.lastAppendTime = now;
        this.retry = true;
    }

    public ProducerRecord producerRecord() {
        return this.producerRecord;
    }

    public RecordDeal recordDeal() {
        return this.recordDeal;
    }

    public CallBack callBack() {
        return this.callBack;
    }

    public ProduceRequestResult futureResult() {
        return this.futureResult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof RecordInDeque)) {
            return false;
        }

        ProducerRecord that = (ProducerRecord) o;
        return this.producerRecord.equals(that);
    }

    @Override
    public int hashCode() {
        return this.producerRecord.hashCode();
    }
}
