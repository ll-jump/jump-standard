package com.jump.standard.commons.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static Object json2Object(String str) {
        Object map = null;
        if (!StringUtils.isEmpty(str)) {
            map = new Gson().fromJson(str, new TypeToken<Object>() {
            }.getType());
            if (map == null) {
                map = new Object();
            }
        }
        return map;
    }

    public static Map<String, String> json2Map(String str) {
        Map<String, String> map = null;
        if (!StringUtils.isEmpty(str)) {
            map = new Gson().fromJson(str, new TypeToken<Map<String, String>>() {
            }.getType());
            if (map == null) {
                map = new HashMap<String, String>();
            }
        }
        return map;
    }

    public static Map<String, Object> json2MapWithObject(String str) {
        Map<String, Object> map = null;
        if (!StringUtils.isEmpty(str)) {
            map = new Gson().fromJson(str, new TypeToken<Map<String, Object>>() {
            }.getType());
            if (map == null) {
                map = new HashMap<String, Object>();
            }
        }
        return map;
    }

    public static List<Object> json2ListWithObject(String str) {
        List<Object> map = null;
        if (!StringUtils.isEmpty(str)) {
            map = new Gson().fromJson(str, new TypeToken<List<Object>>() {
            }.getType());
            if (map == null) {
                map = new ArrayList<Object>();
            }
        }
        return map;
    }

    public static Map<String, List<String>> json2MapWithList(String str) {
        Map<String, List<String>> map = null;
        if (!StringUtils.isEmpty(str)) {
            map = new Gson().fromJson(str, new TypeToken<Map<String, List<String>>>() {
            }.getType());
            if (map == null) {
                map = new HashMap<String, List<String>>();
            }
        }
        return map;
    }

    public static List<Map<String, String>> json2ListWithMap(String str) {
        List<Map<String, String>> list = null;
        if (!StringUtils.isEmpty(str)) {
            list = new Gson().fromJson(str, new TypeToken<List<Map<String, String>>>() {
            }.getType());
            if (list == null) {
                list = new ArrayList<Map<String, String>>();
            }
        }
        return list;
    }

    public static List<Map<String, Object>> json2ListWithMapObject(String str) {
        List<Map<String, Object>> list = null;
        if (!StringUtils.isEmpty(str)) {
            list = new Gson().fromJson(str, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            if (list == null) {
                list = new ArrayList<Map<String, Object>>();
            }
        }
        return list;
    }

    public static Map<String, Map<String, String>> json2MapWithMap(String str) {
        Map<String, Map<String, String>> map = null;
        if (!StringUtils.isEmpty(str)) {
            map = new Gson().fromJson(str, new TypeToken<Map<String, Map<String, String>>>() {
            }.getType());
            if (map == null) {
                map = new HashMap<String, Map<String, String>>();
            }
        }
        return map;
    }

    public static Map<String, List<Map<String, String>>> json2MapWithListMap(String str) {
        Map<String, List<Map<String, String>>> map = null;
        if (!StringUtils.isEmpty(str)) {
            map = new Gson().fromJson(str, new TypeToken<Map<String, List<Map<String, String>>>>() {
            }.getType());
            if (map == null) {
                map = new HashMap<String, List<Map<String, String>>>();
            }
        }
        return map;
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (!StringUtils.isEmpty(json)) {
            return new Gson().fromJson(json, classOfT);
        } else {
            return null;
        }
    }

    public static <T> String toJson(T t) {
        Gson gson = new Gson();
        return gson.toJson(t);
    }
}
