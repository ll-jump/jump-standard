package com.jump.standard.commons.smq.clients.producer;

import com.jump.standard.commons.smq.clients.producer.bo.ProducerRecord;
import com.jump.standard.commons.smq.clients.producer.bo.RecordMetadata;
import com.jump.standard.commons.smq.clients.producer.internals.FutureRecordMetadata;

import java.io.Closeable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author LiLin
 * @desc 生产者接口
 * @create 2021-06-07 10:31
 **/
public interface Producer extends Closeable {
    /**
     * 创建一个不带回调方法的消息
     *
     * @param record     消息
     * @param recordDeal 消息处理接口
     * @return 请求结果
     */
    Future<RecordMetadata> send(ProducerRecord record, RecordDeal recordDeal);

    /**
     * 创建一个带回调方法的消息
     *
     * @param record     消息
     * @param recordDeal 消息处理接口
     * @param callBack   回调接口
     * @return 请求结果
     */
    Future<RecordMetadata> send(ProducerRecord record, RecordDeal recordDeal, CallBack callBack);

    /**
     * 创建一个带回调方法的消息
     * 此方法的失败尝试配置会覆盖SmqProducer的失败尝试配置
     * @param record 消息
     * @param recordDeal 消息处理接口
     * @param callBack 回调接口
     * @param retryMaxTimes 消息失败最大尝试次数
     * @param retryInterval 消息失败尝试间隔
     * @param unit 尝试间隔时间单位
     * @return
     */
    Future<RecordMetadata> send(ProducerRecord record, RecordDeal recordDeal, CallBack callBack, Integer retryMaxTimes, Long retryInterval, TimeUnit unit);

    /**
     * Tries to close the producer cleanly within the specified timeout. If the close does not complete within the
     * timeout, fail any pending send requests and force close the producer.
     */
    void close(long timeout, TimeUnit unit);
}
