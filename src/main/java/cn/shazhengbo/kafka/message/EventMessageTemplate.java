package cn.shazhengbo.kafka.message;

import cn.shazhengbo.kafka.annotation.EventMessage;
import cn.shazhengbo.kafka.config.SysConfig;
import cn.shazhengbo.kafka.utils.json.JsonHelper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: crazyshaqiushi
 * @create_time: 2019/12/10-10:46
 * @description: 爬虫消息发送工具类
 * @version:1.0.0
 */
@Component
public class EventMessageTemplate {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final SysConfig sysConfig;

    public EventMessageTemplate(KafkaTemplate<String, String> kafkaTemplate, SysConfig sysConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.sysConfig = sysConfig;
    }

    /**
     * 发送消息
     *
     * @param object
     * @param clazz
     */
    public void sendMessage(Object object, Class<?> clazz){
        EventMessage message = clazz.getAnnotation(EventMessage.class);
        kafkaTemplate.send(String.format("%s.%s", sysConfig.getTopicPrefix(), message.topic()), JsonHelper.stringify(object));
    }


}
