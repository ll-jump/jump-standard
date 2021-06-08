package com.jump.standard.commons.smq.clients.producer;

import com.jump.standard.commons.smq.clients.producer.bo.ProducerRecord;
import com.jump.standard.commons.smq.clients.producer.bo.RecordDealResult;

/**
 * @author LiLin
 * @desc 消息处理接口
 * @create 2021-06-07 14:10
 **/
public interface RecordDeal<V> {
    /**
     * 处理消息
     * @param producerRecord 消息
     * @return
     */
    RecordDealResult deal(ProducerRecord producerRecord);
}
