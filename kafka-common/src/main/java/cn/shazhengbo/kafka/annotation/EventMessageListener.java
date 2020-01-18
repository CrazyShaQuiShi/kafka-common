package cn.shazhengbo.kafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: crazyshaqiushi
 * @create_time: 2019/12/11-9:23
 * @description:自定义事件订阅器
 * @version:1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventMessageListener {

    /**
     * 消费者群组
     *
     * @return 消费者群组
     */
    String group();

    /**
     * 消费者每次拉取消息数量
     *
     * @return 消费者每次拉取消息数量
     */
    int maxPollRecords() default 10;

    /**
     * 每条消息超时时长,单位毫秒:默认-1分钟
     *
     * @return
     */
    long commitIntervalMs() default 60000L;

    /**
     * poll()的后续调用之间的时间，默认-1分钟
     *
     * @return
     */
    long maxPollIntervalMs() default 60000L;

}
