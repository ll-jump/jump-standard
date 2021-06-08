package com.jump.standard.commons.smq.clients.producer;

import com.jump.standard.commons.smq.clients.producer.bo.RecordMetadata;

/**
 * @author LiLin
 * @desc 回调接口，当一个消息执行完后调用该接口的完成方法
 * @create 2021-06-07 11:02
 **/
public interface CallBack {
    /**
     * 完成
     *
     * @param recordMetadata 请求结果
     * @param exception      异常
     */
    void onCompletion(RecordMetadata recordMetadata, Exception exception);
}
