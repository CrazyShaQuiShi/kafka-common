package cn.shazhengbo.kafka.utils.json;

import cn.shazhengbo.kafka.utils.TrimStringDeserializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author CrazyShaQiuShi
 * @description:JSON辅助类
 * @date 2020/9/7 10:08
 * @version 1.0
 */
@Slf4j
public abstract class JsonHelper {


    public static ObjectMapper OBJECT_MAPPER;


    public static Gson GSON;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.registerModule(new Jdk8Module());
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(String.class, new TrimStringDeserializer());
        OBJECT_MAPPER.registerModules(javaTimeModule, simpleModule);


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }).registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, jsonDeserializationContext) -> {
            String datetime = json.getAsJsonPrimitive().getAsString();
            return LocalDate.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }).registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (localDate, type, jsonSerializationContext) -> {
            String ldtStr = "";

            if (localDate != null) {
                ldtStr = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            return new JsonPrimitive(ldtStr);
        }).registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (localDateTime, type, jsonSerializationContext) -> {
            String ldtStr = "";
            if (localDateTime != null) {
                ldtStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            return new JsonPrimitive(ldtStr);
        }).create();

        GSON = gsonBuilder.create();
    }

    /**
     * 深度clone一个对象
     *
     * @param source 源对象
     * @param <T>    对象类型
     * @return clone的对象
     */
    public static <T> T clone(T source) {
        if (source == null) {
            return null;
        }

        T cloned;
        try {
            cloned = OBJECT_MAPPER.readerFor(source.getClass()).readValue(OBJECT_MAPPER.writeValueAsString(source));
        } catch (IOException ex) {
            throw new RuntimeException("json序列化或反序列化异常", ex);
        }

        return cloned;
    }

    /**
     * 将对象格式化成JSON字符串
     *
     * @param object 对象
     * @return JSON字符串
     */
    public static String stringify(Object object) {
        if (object == null) {
            return "";
        }

        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception ex) {
            throw new RuntimeException("json序列化异常", ex);
        }

        return json;
    }

    /**
     * 将对象格式化成JSON字符串
     *
     * @param object 对象
     * @return JSON字符串
     */
    public static String stringGson(Object object) {
        if (object == null) {
            return "";
        }
        String json;
        try {
            json = GSON.toJson(object);
        } catch (Exception ex) {
            throw new RuntimeException("json序列化异常", ex);
        }

        return json;
    }

    /**
     * 将JSON字符串解析为对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类型
     * @param <T>   对象类型
     * @return 对象
     */
    public static <T> T resolve(String json, Class<T> clazz) {
        T obj;
        try {
            obj = OBJECT_MAPPER.readerFor(clazz).readValue(json);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("json反序列化异常", e);
        }

        return obj;
    }

    /**
     * 将JSON字符串解析为对象
     *
     * @param json  JSON字符串
     * @param clazz 对象类型
     * @param <T>   对象类型
     * @return 对象
     */
    public static <T> T resolveGson(String json, Class<T> clazz) {
        T obj;
        try {
            obj = GSON.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("json反序列化异常", e);
        }

        return obj;
    }

    /**
     * 将JSON字符串解析为对象
     * Type type = new TypeToken<ZhiMaResult<Balance>>() {}.getType();
     *
     * @param json JSON字符串
     * @param type 对象类型
     * @return 对象
     */
    public static <T> T resolveGson(String json, Type type) {
        T obj;
        try {
            obj = GSON.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("json反序列化异常", e);
        }

        return obj;
    }


    /**
     * 将输入流解析成指定类型的对象
     *
     * @param stream 输入流
     * @param clazz  对象类型
     * @param <T>    对象类型
     * @return 对象
     */
    public static <T> T resolve(InputStream stream, Class<T> clazz) {
        T obj;
        try {
            obj = OBJECT_MAPPER.readerFor(clazz).readValue(stream);
        } catch (IOException ex) {
            throw new RuntimeException("解析失败", ex);
        }

        return obj;
    }

    /**
     * 将JSON字符串解析为列表
     *
     * @param json      JSON字符串
     * @param itemClass 列表项类型Class
     * @param converter 字符串到列表项类型转换器
     * @param <T>       列表项类型
     * @return 列表
     */
    public static <T> List<T> resolve(String json, Class<T> itemClass, Function<Object, T> converter) {
        List list;
        List<T> result = new ArrayList<>();
        try {
            list = OBJECT_MAPPER.readerFor(List.class).readValue(json);
            list.forEach(l -> result.add(converter.apply(l)));
        } catch (IOException ex) {
            throw new RuntimeException("解析失败", ex);
        }

        return result;
    }


    /**
     * 将JSON字符串解析为列表
     *
     * @param json      JSON字符串
     * @param itemClass 列表项类型Class
     * @param <T>       列表项类型
     * @return 列表
     */
    public static <T> List<T> resolveListGson(String json, Class<T> itemClass) {
        List<T> list;
        try {
            list = GSON.fromJson(json, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("解析失败", ex);
        }
        return list;
    }


    /**
     * 将源类型对象转换成目标类型对象
     *
     * @param source 源类型对象
     * @param clazz  目标类型
     * @param <S>    源类型
     * @param <T>    目标类型
     * @return 目标类型对象
     */
    public static <S, T> T convert(S source, Class<T> clazz) {
        if (source == null) {
            return null;
        }

        return resolve(stringify(source), clazz);
    }


}
