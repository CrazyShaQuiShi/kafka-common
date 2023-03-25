package cn.shazhengbo.kafka.annotation;

import java.lang.annotation.*;

/**
 * @author: crazyshaqiushi
 * @create_time: 2019/12/10-10:44
 * @description: 爬取消息注解类
 * @version:1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE})
@Documented
@Inherited
public @interface EventMessage {
    /**
     * 消息主题:默认会发送到xx.default中
     * xx:代表配置的主题前缀,具体参考配置
     * {@link cn.shazhengbo.kafka.config.SysConfig}
     *
     * @return
     */
    String topic() default "default";

    /**
     * 默认消息类型-不存储不确认
     *
     * @return
     */
    KafkaMessageType type() default KafkaMessageType.NO_RECORD;

    /**
     * 确认方式:默认不需确认
     *
     * @return
     */
    KafkaAckType ackType() default KafkaAckType.NONE;

    /**
     * 默认:9,最多重试发送9次
     *
     * @return
     */
    int retries() default 8;

    /**
     * 自动重新发送组消息次数
     *
     * @return
     */
    int maxAckCount() default 8;
}
