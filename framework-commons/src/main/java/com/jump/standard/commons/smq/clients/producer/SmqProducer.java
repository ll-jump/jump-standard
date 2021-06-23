package com.jump.standard.commons.smq.clients.producer;

import com.jump.standard.commons.smq.clients.producer.bo.ProducerRecord;
import com.jump.standard.commons.smq.clients.producer.bo.RecordMetadata;
import com.jump.standard.commons.smq.clients.producer.exceptions.SmqException;
import com.jump.standard.commons.smq.clients.producer.internals.RecordAccumulator;
import com.jump.standard.commons.smq.clients.producer.internals.Sender;
import com.jump.standard.commons.smq.clients.producer.threads.SmqThread;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author LiLin
 * @desc smq生产者
 * @create 2021-06-07 10:31
 **/
public class SmqProducer implements Producer {
    private static final Logger log = LoggerFactory.getLogger(SmqProducer.class);

    /**
     * clientId自增序列
     */
    private static final AtomicInteger PRODUCER_CLIENT_ID_SEQUENCE = new AtomicInteger(1);
    /**
     * 客户端id
     */
    private String clientId;
    /**
     * 消息存储队列处理组件
     */
    private final RecordAccumulator recordAccumulator;
    /**
     * 消息发送组件
     */
    private final Sender sender;
    /**
     * 执行发送消息的线程
     */
    private final Thread ioThread;
    /**
     * 最大重试次数
     */
    private final int retryMaxTimes;
    /**
     * 重试间隔 单位ms
     */
    private final long retryInterval;
    /**
     * 消息处理是否强制按顺序
     */
    private final boolean strongInOrder;

    public SmqProducer(Map<String, Object> configs) {
        this(new ProducerConfig(configs));
    }

    public SmqProducer(Properties properties) {
        this(new ProducerConfig(properties));
    }

    private SmqProducer(ProducerConfig config) {
        try {
            log.info("Starting the smq producer");
            this.clientId = config.getString(ProducerConfig.CLIENT_ID);
            if (StringUtils.isBlank(clientId)) {
                this.clientId = "producer-" + PRODUCER_CLIENT_ID_SEQUENCE.getAndIncrement();
            }
            this.retryMaxTimes = config.getInt(ProducerConfig.RETRY_MAX_TIMES);
            this.retryInterval = config.getLong(ProducerConfig.RETRY_INTERVAL);
            this.strongInOrder = config.getBoolean(ProducerConfig.STRONG_IN_ORDER);
            this.recordAccumulator = new RecordAccumulator();
            //启动处理消息的线程
            this.sender = new Sender(recordAccumulator, strongInOrder);
            String ioThreadName = "smp-producer-network-thread" + (clientId.length() > 0 ? " | " + clientId : "");
            this.ioThread = new SmqThread(ioThreadName, this.sender, true);
            this.ioThread.start();
            log.info("Ths smq producer started");
        } catch (Throwable t) {
            close(0, TimeUnit.MILLISECONDS, true);
            throw new SmqException("Failed to construct smq producer", t);
        }
    }

    /**
     * 创建一个不带回调方法的消息
     *
     * @param record     消息
     * @param recordDeal 消息处理接口
     * @return 请求结果
     */
    @Override
    public Future<RecordMetadata> send(ProducerRecord record, RecordDeal recordDeal) {
        return send(record, recordDeal, null);
    }

    /**
     * 创建一个带回调方法的消息
     *
     * @param record     消息
     * @param recordDeal 消息处理接口
     * @param callBack   回调接口
     * @return 请求结果
     */
    @Override
    public Future<RecordMetadata> send(ProducerRecord record, RecordDeal recordDeal, CallBack callBack) {
        return send(record, recordDeal, callBack, null, null, null);
    }

    /**
     * 创建一个带回调方法的消息
     *
     * @param record        消息
     * @param recordDeal    消息处理接口
     * @param callBack      回调接口
     * @param retryMaxTimes 消息失败最大尝试次数
     * @param retryInterval 消息失败尝试间隔
     * @param unit          尝试间隔时间单位
     * @return
     */
    @Override
    public Future<RecordMetadata> send(ProducerRecord record, RecordDeal recordDeal, CallBack callBack, Integer retryMaxTimes, Long retryInterval, TimeUnit unit) {
        if (retryMaxTimes == null) {
            retryMaxTimes = this.retryMaxTimes;
        }
        if (retryInterval == null) {
            retryInterval = this.retryInterval;
        } else {
            retryInterval = unit.toMillis(retryInterval);
        }
        //发送消息
        return this.recordAccumulator.append(record, recordDeal, callBack, retryMaxTimes, retryInterval, this.strongInOrder);
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() {
        // 客户端关闭
        close(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    /**
     * Tries to close the producer cleanly within the specified timeout. If the close does not complete within the
     * timeout, fail any pending send requests and force close the producer.
     *
     * @param timeout
     * @param unit
     */
    @Override
    public void close(long timeout, TimeUnit unit) {
        close(timeout, unit, false);
    }

    /**
     * 关闭客户端
     *
     * @param timeout          超时时间
     * @param unit             时间单位
     * @param swallowException 是否忽略异常
     */
    public void close(long timeout, TimeUnit unit, boolean swallowException) {
        //客户端关闭
        if (timeout < 0) {
            throw new IllegalArgumentException("The timeout cannot be negative.");
        }
        // this will keep track of the first encountered exception
        AtomicReference<Throwable> firstException = new AtomicReference<Throwable>();
        boolean invokedFromCallback = Thread.currentThread() == this.ioThread;
        if (timeout > 0) {
            if (invokedFromCallback) {
                log.warn("Overriding close timeout {} ms to 0 ms in order to prevent useless blocking due to self-join. " +
                        "This means you have incorrectly invoked close with a non-zero timeout from the producer call-back.", timeout);
            } else {
                // Try to close gracefully.
                if (this.sender != null) {
                    this.sender.initiateClose();
                }
                if (this.ioThread != null) {
                    try {
                        this.ioThread.join(unit.toMillis(timeout));
                    } catch (InterruptedException t) {
                        firstException.compareAndSet(null, t);
                        log.error("Interrupted while joining ioThread", t);
                    }
                }
            }
        }

        if (this.sender != null && this.ioThread != null && this.ioThread.isAlive()) {
            log.info("Proceeding to force close the producer since pending requests could not be completed " +
                    "within timeout {} ms.", timeout);
            this.sender.forceClose();
            // Only join the sender thread when not calling from callback.
            if (!invokedFromCallback) {
                try {
                    this.ioThread.join();
                } catch (InterruptedException e) {
                    firstException.compareAndSet(null, e);
                }
            }
        }

        log.info("The smq producer has closed.");
        if (firstException.get() != null && !swallowException) {
            throw new SmqException("Failed to close smq producer", firstException.get());
        }
    }
}
