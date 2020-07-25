package com.jump.standard.redis.service.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;

/**
 * 〈Kryo序列化类〉
 *
 * @author LiLin
 * @date 2020/7/3 0003
 */
public class KryoSerializer {
    public KryoSerializer() {
    }

    public static <T> T clone(T object, Class<T> cls) {
        return deserialize(serialize(object, cls), cls);
    }

    public static <T> void serialize(T obj, Class<T> cls, OutputStream outputStream) {
        Validate.isTrue(outputStream != null, "The OutputStream must not be null", new Object[0]);
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.register(cls, new BeanSerializer(kryo, cls));
        Output out = new Output(outputStream);

        try {
            kryo.writeObject(out, obj);
            out.flush();
        } finally {
            out.close();
        }

    }

    public static <T> byte[] serialize(T obj, Class<T> cls) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        serialize(obj, cls, baos);
        return baos.toByteArray();
    }

    public static <T> byte[] serializationList(List<T> obj, Class<T> clazz) {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        CollectionSerializer serializer = new CollectionSerializer();
        kryo.register(ArrayList.class, serializer);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        Output out = new Output(baos);

        try {
            kryo.writeObject(out, obj);
            out.flush();
        } finally {
            out.close();
        }

        return baos.toByteArray();
    }

    public static <T> T deserialize(InputStream inputStream, Class<T> cls) {
        Validate.isTrue(inputStream != null, "The InputStream must not be null", new Object[0]);
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.register(cls, new BeanSerializer(kryo, cls));
        Input input = new Input(inputStream);

        T var4;
        try {
            var4 = kryo.readObject(input, cls);
        } finally {
            input.close();
        }

        return var4;
    }

    public static <T> T deserialize(byte[] objectData, Class<T> cls) {
        Validate.isTrue(objectData != null, "The byte[] must not be null", new Object[0]);
        return deserialize((InputStream)(new ByteArrayInputStream(objectData)), cls);
    }

    public static <T> List<T> deserializationList(byte[] objectData, Class<T> clazz) {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        CollectionSerializer serializer = new CollectionSerializer();
        kryo.register(ArrayList.class, serializer);
        ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
        Input input = new Input(bais);
        return (List)kryo.readObject(input, ArrayList.class, serializer);
    }

    public static void main(String[] args) throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        List<String> list = new ArrayList();
        list.add("A");
        list.add("B");
        list.add("C");
        byte[] bytes = serializationList(list, String.class);
        watch.stop();
        System.out.println(bytes.length);
        System.out.println("serialize: " + watch.getTime() + " ms.");
        watch.reset();
        watch.start();
        List<String> copy = deserializationList(bytes, String.class);
        watch.stop();
        System.out.println(copy);
        System.out.println("deserialize: " + watch.getTime() + " ms.");
    }
}