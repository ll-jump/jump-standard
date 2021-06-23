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
    /**
     * 消息处理是否强制按顺序，默认为false（false时消息正常处理为顺序处理，但是重试消息优先级低于正常消息；true时，严格按照顺序处理，重试消息优先级高于正常消息）
     * 此属性需谨慎配置，如果业务不是必须严格顺序处理，不建议配置该属性为true，因为配置后，重试消息会阻塞重试间隔时间，导致之后发送的正常消息也被阻塞了
     */
    public static final String STRONG_IN_ORDER = "strong.in.order";

    static {
        CONFIG = new ConfigDef().define(CLIENT_ID, ConfigDef.Type.STRING, "")
                .define(RETRY_MAX_TIMES, ConfigDef.Type.INT, 0)
                .define(RETRY_INTERVAL, ConfigDef.Type.LONG, 0L)
                .define(STRONG_IN_ORDER, ConfigDef.Type.BOOLEAN, false);
    }

    ProducerConfig(Map<?, ?> props) {
        super(CONFIG, props);
    }
}
