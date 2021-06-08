package com.jump.standard.commons.smq.clients.producer;

import com.jump.standard.commons.smq.clients.producer.common.config.AbstractConfig;
import com.jump.standard.commons.smq.clients.producer.common.config.ConfigDef;

import java.util.Map;

/**
 * @author LiLin
 * @desc 生产者配置
 * @create 2021-06-07 14:28
 **/
public class ProducerConfig extends AbstractConfig {
    private static final ConfigDef CONFIG;
    public static final String CLIENT_ID = "client.id";
    /**
     * 重试最大次数
     */
    public static final String RETRY_MAX_TIMES = "retry.max.times";
    /**
     * 重试间隔 单位ms
     */
    public static final String RETRY_INTERVAL = "retry.interval";

    static {
        CONFIG = new ConfigDef().define(CLIENT_ID, ConfigDef.Type.STRING, "")
                .define(RETRY_MAX_TIMES, ConfigDef.Type.INT, 0)
                .define(RETRY_INTERVAL, ConfigDef.Type.LONG, 0L);
    }

    ProducerConfig(Map<?, ?> props) {
        super(CONFIG, props);
    }
}
