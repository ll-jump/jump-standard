package com.jump.standard.commons.smq.clients.producer.internals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LiLin
 * @desc 消息发送组件
 * @create 2021-06-07 18:20
 **/
public class Sender implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Sender.class);

    /**
     * 消息存储队列处理组件
     */
    private final RecordAccumulator recordAccumulator;
    /**
     * sender线程运行状态
     */
    private volatile boolean running;
    /**
     * 是否强制关闭sender线程
     */
    private volatile boolean forceClose;

    public Sender(RecordAccumulator recordAccumulator) {
        this.recordAccumulator = recordAccumulator;
        this.running = true;
    }

    @Override
    public void run() {
        log.info("Staring smp producer I/O thread.");
        while (running) {
            run(false);
        }
        log.info("Beginning shutdown of smp producer I/O thread, sending remaining records.");

        //处理完消息队列及消息重试队列
        while (!forceClose && (this.recordAccumulator.hasUnDeal())) {
            run(true);
        }

        if (forceClose) {
            //强制关闭，拒绝所有正在处理的消息
            this.recordAccumulator.abortIncompleteRecords();
        }
    }

    private void run(boolean close) {
        try {
            this.recordAccumulator.deal(close);
        } catch (Exception e) {
            log.error("Uncaught error in smp producer I/O thread: ", e);
        }

    }

    public void setForceClose(boolean forceClose) {
        this.forceClose = forceClose;
    }

    /**
     * 关闭线程
     * 等待所有请求处理完后关闭
     */
    public void initiateClose() {
        this.running = false;
        this.recordAccumulator.close();
        this.recordAccumulator.signalDealCondition();
    }

    /**
     * 强制关闭
     */
    public void forceClose() {
        this.forceClose = true;
        initiateClose();
    }
}
