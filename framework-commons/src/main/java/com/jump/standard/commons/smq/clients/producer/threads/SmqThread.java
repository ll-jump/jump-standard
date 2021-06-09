package com.jump.standard.commons.smq.clients.producer.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LiLin
 * @desc smq线程包装类
 * @create 2021-06-07 18:34
 **/
public class SmqThread extends Thread {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public SmqThread(final String name, Runnable runnable, boolean daemon) {
        super(runnable, name);
        //是否守护进程
        setDaemon(daemon);
        setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                log.error("Uncaught exception in " + name + ": ", e);
            }
        });
    }
}
