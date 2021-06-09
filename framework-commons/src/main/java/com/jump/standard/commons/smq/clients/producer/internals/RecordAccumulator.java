package com.jump.standard.commons.smq.clients.producer.internals;

import com.jump.standard.commons.smq.clients.producer.CallBack;
import com.jump.standard.commons.smq.clients.producer.RecordDeal;
import com.jump.standard.commons.smq.clients.producer.bo.ProducerRecord;
import com.jump.standard.commons.smq.clients.producer.enums.RetryState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LiLin
 * @desc 消息存储队列处理组件
 * @create 2021-06-07 19:57
 **/
public class RecordAccumulator {
    private static final Logger log = LoggerFactory.getLogger(RecordAccumulator.class);
    /**
     * 是否关闭
     */
    private volatile boolean closed;
    /**
     * 正在发送消息的线程
     */
    private final AtomicInteger appendInProgress;
    /**
     * 消息队列
     */
    private final Deque<RecordInDeque> records;
    /**
     * 重试消息队列
     */
    private final Deque<RecordInDeque> retryRecords;
    /**
     * 需处理消息数量
     */
    private volatile AtomicInteger recordsSize;
    /**
     * 重试消息数量
     */
    private volatile AtomicInteger retrySize;
    /**
     * 正在处理中的消息
     */
    private final IncompleteRecord incomplete;
    /**
     * 消息处理唤醒条件
     */
    private final Condition dealCondition;
    /**
     * 消息重试唤醒条件
     */
    private final Condition retryCondition;
    /**
     * 可重入锁
     */
    private final ReentrantLock lock;

    public RecordAccumulator() {
        this.closed = false;
        this.appendInProgress = new AtomicInteger(0);
        this.records = new ArrayDeque<>();
        this.retryRecords = new ArrayDeque<>();
        this.incomplete = new IncompleteRecord();
        this.lock = new ReentrantLock();
        this.dealCondition = this.lock.newCondition();
        this.retryCondition = this.lock.newCondition();
        this.recordsSize = new AtomicInteger(0);
        this.retrySize = new AtomicInteger(0);
    }

    /**
     * 在消息队列中追加一条消息
     *
     * @param record        消息
     * @param recordDeal    消息处理接口
     * @param callBack      消息处理完成回调接口
     * @param retryMaxTimes 最大重试次数
     * @param retryInterval 重试间隔 单位ms
     * @return
     */
    public FutureRecordMetadata append(ProducerRecord record, RecordDeal recordDeal, CallBack callBack, int retryMaxTimes, long retryInterval) {
        appendInProgress.incrementAndGet();
        try {
            synchronized (records) {
                if (closed) {
                    throw new IllegalStateException("Cannot send after the producer is closed.");
                }
                RecordInDeque recordInDeque = new RecordInDeque(record, recordDeal, callBack, retryMaxTimes, retryInterval, System.currentTimeMillis());
                this.records.add(recordInDeque);
                this.recordsSize.incrementAndGet();
                if (this.recordsSize.get() <= 1) {
                    //如果消息队列中消息已全部处理完成，此时发送的是第一条消息
                    this.lock.lock();
                    try {
                        //唤醒消息处理线程
                        this.dealCondition.signal();
                    } finally {
                        this.lock.unlock();
                    }
                }

                this.incomplete.add(recordInDeque);
                if (this.retrySize.get() > 0) {
                    //如果存在重试消息
                    this.lock.lock();
                    try {
                        //唤醒消息重试线程
                        this.retryCondition.signal();
                    } finally {
                        this.lock.unlock();
                    }
                }

                return new FutureRecordMetadata(recordInDeque.producerRecord().group(), recordInDeque.producerRecord().key(), recordInDeque.producerRecord().timestamp(), recordInDeque.futureResult());
            }
        } finally {
            appendInProgress.decrementAndGet();
        }
    }

    /**
     * 处理消息
     *
     * @param close 是否关闭客户端调用
     */
    public void deal(boolean close) {
        //处理重试消息
        dealRetryRecord(false);
        //如果消息队列中无消息，且重试队列中有消息，则处理重试队列消息；防止一直无消息发送，导致线程一直卡在records.take()，无法处理重试消息
        while (this.records.size() <= 0 && this.retryRecords.size() > 0) {
            dealRetryRecord(true);
        }
        //处理消息
        RecordInDeque recordInDeque = null;
        this.lock.lock();
        try {
            recordInDeque = records.pollFirst();
            if (recordInDeque == null && !close) {
                try {
                    this.dealCondition.await();
                    recordInDeque = records.pollFirst();
                } catch (InterruptedException e) {
                    log.error("处理消息线程阻塞等待异常", e);
                }
            }
            if (recordInDeque != null) {
                this.recordsSize.decrementAndGet();
            }
        } finally {
            this.lock.unlock();
        }
        if (recordInDeque != null) {
            dealRecord(recordInDeque);
        }
    }

    /**
     * 将正在处理的消息全部拒绝处理
     */
    public void abortIncompleteRecords() {
        do {
            abortRecords();
        } while (!appendsInProgress());
    }

    /**
     * 将消息全部拒绝处理
     */
    private void abortRecords() {
        long now = System.currentTimeMillis();
        for (RecordInDeque recordInDeque : incomplete.all()) {
            recordInDeque.done(now, new IllegalStateException("Producer is closed forcefully."));
        }
    }

    /**
     * 处理重试消息
     *
     * @param await 是否阻塞线程
     */
    private void dealRetryRecord(boolean await) {
        if (this.retrySize.get() > 0) {
            long now = System.currentTimeMillis();
            RecordInDeque recordInDeque;
            RetryState retryState;
            synchronized (retryRecords) {
                //存在需要重试的消息
                recordInDeque = retryRecords.peekFirst();
                //获取该重试消息状态
                retryState = recordInDeque.retryState(now);
                if (retryState == RetryState.CAN_RETRY || retryState == RetryState.NO_OVER_LIMIT) {
                    // 可以重试 或者 已过最大重试次数，不可重试，且需要从重试队列中移除
                    recordInDeque = retryRecords.pollFirst();
                    this.retrySize.decrementAndGet();
                }
            }

            if (await && retryState == RetryState.NO_INTERVAL) {
                this.lock.lock();
                try {
                    //此处线程阻塞重试间隔剩余时间，如果消息队列中插入数据，则唤醒线程
                    long leftRetryInterval = recordInDeque.leftRetryInterval(now);
                    if (leftRetryInterval > 0) {
                        try {
                            //线程阻塞剩余重试时间间隔或消息队列中有消息加入唤醒该线程
                            retryCondition.await(leftRetryInterval, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            log.error("重试线程阻塞等待异常", e);
                        }
                    }
                } finally {
                    this.lock.unlock();
                }
            }

            if (retryState == RetryState.CAN_RETRY) {
                recordInDeque.retry(System.currentTimeMillis());
                //处理消息
                dealRecord(recordInDeque);
            }
        }
    }

    /**
     * 处理消息
     *
     * @param recordInDeque
     */
    private void dealRecord(RecordInDeque recordInDeque) {
        boolean retry = recordInDeque.deal();
        if (retry) {
            //需要再次重试，重新加入重试队列
            recordInDeque.setRetry(System.currentTimeMillis());
            this.retryRecords.addLast(recordInDeque);
            this.retrySize.incrementAndGet();
        } else {
            //消息处理完成，从正在处理中的消息集合中移除
            this.incomplete.remove(recordInDeque);
            //释放资源
            recordInDeque = null;
        }
    }

    public void close() {
        this.closed = true;
    }

    /**
     * 是否存在正在发送消息的线程
     *
     * @return
     */
    private boolean appendsInProgress() {
        return appendInProgress.get() > 0;
    }

    /**
     * 获取是否还有未处理的消息
     *
     * @return
     */
    public boolean hasUnDeal() {
        return this.records.size() > 0 || this.retryRecords.size() > 0;
    }


    /**
     * 处理中的消息
     */
    private final static class IncompleteRecord {
        private final Set<RecordInDeque> incomplete;


        private IncompleteRecord() {
            this.incomplete = new HashSet<>();
        }

        public void add(RecordInDeque recordInDeque) {
            synchronized (incomplete) {
                this.incomplete.add(recordInDeque);
            }
        }

        public void remove(RecordInDeque recordInDeque) {
            synchronized (incomplete) {
                boolean removed = this.incomplete.remove(recordInDeque);
                if (!removed) {
                    throw new IllegalStateException("Remove from the incomplete set failed. This should be impossible.");
                }
            }
        }

        public Iterable<RecordInDeque> all() {
            synchronized (incomplete) {
                return new ArrayList<>(this.incomplete);
            }
        }
    }
}
