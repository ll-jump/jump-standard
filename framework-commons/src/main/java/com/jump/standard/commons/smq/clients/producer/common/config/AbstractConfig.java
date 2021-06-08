package com.jump.standard.commons.smq.clients.producer.common.config;

import com.jump.standard.commons.smq.clients.producer.common.config.types.Password;
import com.jump.standard.commons.smq.clients.producer.common.utils.Utils;
import com.jump.standard.commons.smq.clients.producer.exceptions.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author LiLin
 * @desc 配置抽象类
 * @create 2021-06-07 14:47
 **/
public class AbstractConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 用户传入的原始配置
     */
    private final Map<String, ?> originals;

    /**
     * 解析转换后的配置
     */
    private final Map<String, Object> values;

    public AbstractConfig(ConfigDef definition, Map<?, ?> orignals) {
        this(definition, orignals, true);
    }

    public AbstractConfig(ConfigDef definition, Map<?, ?> originals, boolean doLog) {
        /**
         * originals 的key类型需要都为String
         */
        for (Object key : originals.keySet()) {
            if (!(key instanceof String)) {
                throw new ConfigException(key.toString(), originals.get(key), "Key must be a string.");
            }
        }
        this.originals = (Map<String, ?>) originals;
        this.values = definition.parse(this.originals);
        //打印日志
        if (doLog) {
            logAll();
        }
    }

    protected Object get(String key) {
        if (!values.containsKey(key)) {
            throw new ConfigException(String.format("Unknown configuration '%s'", key));
        }
        return values.get(key);
    }

    public Short getShort(String key) {
        return (Short) get(key);
    }

    public Integer getInt(String key) {
        return (Integer) get(key);
    }

    public Long getLong(String key) {
        return (Long) get(key);
    }

    public Double getDouble(String key) {
        return (Double) get(key);
    }

    @SuppressWarnings("unchecked")
    public List<String> getList(String key) {
        return (List<String>) get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public Password getPassword(String key) {
        return (Password) get(key);
    }

    public Class<?> getClass(String key) {
        return (Class<?>) get(key);
    }


    /**
     * 打印所有配置日志
     */
    private void logAll() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append(" values: ");
        builder.append(Utils.NL);
        for (Map.Entry<String, Object> entry : this.values.entrySet()) {
            builder.append('\t');
            builder.append(entry.getKey());
            builder.append(" = ");
            builder.append(entry.getValue());
            builder.append(Utils.NL);
        }
        log.info(builder.toString());
    }
}
