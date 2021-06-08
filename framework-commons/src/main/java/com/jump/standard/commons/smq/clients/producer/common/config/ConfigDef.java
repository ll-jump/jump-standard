package com.jump.standard.commons.smq.clients.producer.common.config;

import com.jump.standard.commons.smq.clients.producer.common.config.types.Password;
import com.jump.standard.commons.smq.clients.producer.common.utils.Utils;
import com.jump.standard.commons.smq.clients.producer.exceptions.ConfigException;

import java.util.*;

/**
 * @author LiLin
 * @desc 配置默认值处理类
 * @create 2021-06-07 14:52
 **/
public class ConfigDef {
    /**
     * 默认值
     */
    private static final Object NO_DEFAULT_VALUE = new String("");
    private final Map<String, ConfigKey> configKeys = new HashMap();

    /**
     * 设置默认值
     *
     * @param name
     * @param type
     * @param defaultValue
     * @return
     */
    public ConfigDef define(String name, Type type, Object defaultValue) {
        if (configKeys.containsKey(name)) {
            throw new ConfigException("Configuration " + name + " is defined twice");
        }
        Object parseDefault = defaultValue == NO_DEFAULT_VALUE ? NO_DEFAULT_VALUE : parseType(name, defaultValue, type);
        configKeys.put(name, new ConfigKey(name, type, parseDefault));
        return this;
    }

    /**
     * 将配置转为默认值指定的数据类型，如果没有配置相关配置，则赋值默认值
     *
     * @param props
     * @return
     */
    public Map<String, Object> parse(Map<?, ?> props) {
        Map<String, Object> values = new HashMap<>();
        for (ConfigKey key : configKeys.values()) {
            Object value;
            if (props.containsKey(key.name)) {
                //如果配置了该参数，则将值转为相应的数据类型
                value = parseType(key.name, props.get(key.name), key.type);
            } else if (key.defaultValue == NO_DEFAULT_VALUE) {
                //如果既没有配置参数，又没有指定默认值，则抛异常
                throw new ConfigException("Missing required configuration \"" + key.name + "\" which has no default value.");
            } else {
                //否则取默认值
                value = key.defaultValue;
            }
            values.put(key.name, value);
        }

        return values;
    }

    /**
     * 将值转为指定类型
     *
     * @param name
     * @param value
     * @param type
     * @return
     */
    private Object parseType(String name, Object value, Type type) {
        try {
            if (value == null) {
                return null;
            }

            String trimmed = null;
            if (value instanceof String) {
                trimmed = ((String) value).trim();
            }

            switch (type) {
                case BOOLEAN:
                    if (value instanceof String) {
                        if (trimmed.equalsIgnoreCase("true")) {
                            return true;
                        } else if (trimmed.equalsIgnoreCase("false")) {
                            return false;
                        } else {
                            throw new ConfigException(name, value, "Expected value to be either true or false");
                        }
                    } else if (value instanceof Boolean) {
                        return value;
                    } else {
                        throw new ConfigException(name, value, "Expected value to be either true or false");
                    }
                case PASSWORD:
                    if (value instanceof Password) {
                        return value;
                    } else if (value instanceof String) {
                        return new Password(trimmed);
                    } else {
                        throw new ConfigException(name, value, "Expected value to be a string, but it was a " + value.getClass().getName());
                    }
                case STRING:
                    if (value instanceof String) {
                        return trimmed;
                    } else {
                        throw new ConfigException(name, value, "Expected value to be a string, but it was a " + value.getClass().getName());
                    }
                case INT:
                    if (value instanceof Integer) {
                        return value;
                    } else if (value instanceof String) {
                        return Integer.parseInt(trimmed);
                    } else {
                        throw new ConfigException(name, value, "Expected value to be an number.");
                    }
                case SHORT:
                    if (value instanceof Short) {
                        return value;
                    } else if (value instanceof String) {
                        return Short.parseShort(trimmed);
                    } else {
                        throw new ConfigException(name, value, "Expected value to be an number.");
                    }
                case LONG:
                    if (value instanceof Integer) {
                        return ((Integer) value).longValue();
                    }
                    if (value instanceof Long) {
                        return value;
                    } else if (value instanceof String) {
                        return Long.parseLong(trimmed);
                    } else {
                        throw new ConfigException(name, value, "Expected value to be an number.");
                    }
                case DOUBLE:
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    } else if (value instanceof String) {
                        return Double.parseDouble(trimmed);
                    } else {
                        throw new ConfigException(name, value, "Expected value to be an number.");
                    }
                case LIST:
                    if (value instanceof List) {
                        return value;
                    } else if (value instanceof String) {
                        if (trimmed.isEmpty()) {
                            return Collections.emptyList();
                        } else {
                            return Arrays.asList(trimmed.split("\\s*,\\s*", -1));
                        }
                    } else {
                        throw new ConfigException(name, value, "Expected a comma separated list.");
                    }
                case CLASS:
                    if (value instanceof Class) {
                        return value;
                    } else if (value instanceof String) {
                        return Class.forName(trimmed, true, Utils.getContextOrSmqClassLoader());
                    } else {
                        throw new ConfigException(name, value, "Expected a Class instance or class name.");
                    }
                default:
                    throw new IllegalStateException("Unknown type.");
            }
        } catch (NumberFormatException e) {
            throw new ConfigException(name, value, "Not a number of type " + type);
        } catch (ClassNotFoundException e) {
            throw new ConfigException(name, value, "Class " + value + " could not be found.");
        }
    }

    public static class ConfigKey {
        public final String name;
        public final Type type;
        public final Object defaultValue;

        public ConfigKey(String name, Type type, Object defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }
    }

    public enum Type {
        BOOLEAN, STRING, INT, SHORT, LONG, DOUBLE, LIST, CLASS, PASSWORD
    }
}
