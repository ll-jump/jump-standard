package com.jump.standard.commons.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 〈jaxb工具类〉
 *
 * @author LiLin
 * @date 2020/4/30 0030
 */
public class JaxbUtil {
    public static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * 对象转xml字符串
     */
    public static String objToXml(Object obj) throws JAXBException {
        return objToXml(obj, DEFAULT_ENCODING);
    }

    /**
     * 对象转xml字符串
     */
    public static String objToXml(Object obj, String encoding) throws JAXBException {
        String result = null;
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = context.createMarshaller();
        // 指定是否使用换行和缩排对已编组 XML 数据进行格式化的属性名称。
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        result = writer.toString();
        return result;
    }

    /**
     * xml转对象
     * @param xml
     * @param t
     * @param <T>
     * @return
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToObj(String xml, Class<T> t) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(t);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        T obj = (T) unmarshaller.unmarshal(new StringReader(xml));
        return obj;
    }
}