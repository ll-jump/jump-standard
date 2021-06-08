package com.jump.standard.commons.smq.clients.producer.bo;

import com.jump.standard.commons.smq.clients.producer.enums.DealResult;

/**
 * @author LiLin
 * @desc 消息处理结果
 * @create 2021-06-07 14:15
 **/
public class RecordDealResult {
    /**
     * 处理结果
     */
    private final DealResult dealResult;
    /**
     * 异常
     */
    private final RuntimeException error;

    public RecordDealResult(DealResult dealResult, RuntimeException error) {
        this.dealResult = dealResult;
        this.error = error;
    }

    public DealResult getDealResult() {
        return dealResult;
    }

    public RuntimeException getError() {
        return error;
    }
}
