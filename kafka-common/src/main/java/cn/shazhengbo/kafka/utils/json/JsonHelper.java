package cn.shazhengbo.kafka.utils.json;


import cn.shazhengbo.kafka.utils.TrimStringDeserializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * JSON辅助类
 */
public abstract class JsonHelper {

    public static ObjectMapper OBJECT_MAPPER;
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonHelper.class);

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(String.class, new TrimStringDeserializer());
        OBJECT_MAPPER.registerModule(simpleModule);
    }

    /**
     * 深度clone一个对象
     * @param source 源对象
     * @param <T> 对象类型
     * @return clone的对象
     */
    public static <T> T clone(T source) {
        if (source == null) {
            return null;
        }

        T cloned;
        try {
            cloned = OBJECT_MAPPER.readerFor(source.getClass()).readValue(OBJECT_MAPPER.writeValueAsString(source));
        }
        catch (IOException ex) {
            throw new RuntimeException("json序列化或反序列化异常", ex);
        }

        return cloned;
    }

    /**
     * 将对象格式化成JSON字符串
     * @param object 对象
     * @return JSON字符串
     */
    public static String stringify(Object object){
        if (object == null) {
            return "";
        }

        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(object);
        }
        catch (Exception ex) {
            throw new RuntimeException("json序列化异常", ex);
        }

        return json;
    }

    /**
     * 将JSON字符串解析为对象
     * @param json JSON字符串
     * @param clazz 对象类型
     * @param <T> 对象类型
     * @return 对象
     */
    public static <T> T resolve(String json, Class<T> clazz) {
        T obj;
        try {
            obj = OBJECT_MAPPER.readerFor(clazz).readValue(json);
        }
        catch (Exception e) {
            throw new RuntimeException("json反序列化异常", e);
        }

        return obj;
    }

    /**
     * 将输入流解析成指定类型的对象
     * @param stream 输入流
     * @param clazz 对象类型
     * @param <T> 对象类型
     * @return 对象
     */
    public static <T> T resolve(InputStream stream, Class<T> clazz) {
        T obj;
        try {
            obj  = OBJECT_MAPPER.readerFor(clazz).readValue(stream);
        }
        catch (IOException ex) {
            throw new RuntimeException("解析失败", ex);
        }

        return obj;
    }

    /**
     * 将JSON字符串解析为列表
     * @param json JSON字符串
     * @param itemClass 列表项类型Class
     * @param converter 字符串到列表项类型转换器
     * @param <T> 列表项类型
     * @return 列表
     */
    public static <T> List<T> resolve(String json, Class<T> itemClass, Function<String, T> converter) {
        List list;
        List<T> result = new ArrayList<>();
        try {
            list = OBJECT_MAPPER.readerFor(List.class).readValue(json);
            list.forEach(l -> result.add(converter.apply(l.toString())));
        }
        catch (IOException ex) {
            throw new RuntimeException("解析失败", ex);
        }

        return result;
    }

    /**
     * 将源类型对象转换成目标类型对象
     * @param source 源类型对象
     * @param clazz 目标类型
     * @param <S> 源类型
     * @param <T> 目标类型
     * @return 目标类型对象
     */
    public static <S, T> T convert(S source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

        return resolve(stringify(source), clazz);
    }

}
