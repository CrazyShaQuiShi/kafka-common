package cn.shazhengbo.kafka.message;

import cn.shazhengbo.kafka.annotation.BaseGlobalEntity;
import cn.shazhengbo.kafka.annotation.EventMessage;
import cn.shazhengbo.kafka.annotation.KafkaMessageType;
import cn.shazhengbo.kafka.config.SysConfig;
import cn.shazhengbo.kafka.store.AbstractMessageStore;
import cn.shazhengbo.kafka.utils.json.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: crazyshaqiushi
 * @create_time: 2019/12/10-10:46
 * @description: 爬虫消息发送工具类
 * @version:1.0.0
 */
@Slf4j
@Component
public class EventMessageTemplate {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final SysConfig sysConfig;
    @Autowired(required = false)
    private AbstractMessageStore eventMessageStore;

    @Autowired
    public EventMessageTemplate(KafkaTemplate kafkaTemplate, SysConfig sysConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.sysConfig = sysConfig;
    }

    /**
     * 异步发送
     *
     * @param object
     * @param clazz
     * @throws Exception
     */
    public void sendMessage(Object object, Class<?> clazz) throws Exception {
        EventMessage message = clazz.getAnnotation(EventMessage.class);
        String completeTopic = String.format("%s.%s", sysConfig.getTopicPrefix(), message.topic());
        String messageText = JsonHelper.stringify(object);
        /**
         * 需要记录数据的
         */
        if (KafkaMessageType.RECPRD.equals(message.type())) {
            sendMessageStorage(object, message, completeTopic, messageText);
        } else if (KafkaMessageType.NO_RECORD.equals(message.type())) {
            sendMessageNoRecord(completeTopic, messageText);
        }
    }

    /**
     * 直接发送消息
     *
     * @param completeTopic
     * @param messageText
     */
    private void sendMessageNoRecord(String completeTopic, String messageText) {
        /**
         * 发送数据
         */
        try {
            kafkaTemplate.send(completeTopic, messageText);
        } catch (Exception e) {
            log.error("发送消息异常:{}", e.getMessage());
        }
    }

    /**
     * 有记录消息发送
     *
     * @param object
     * @param message
     * @param completeTopic
     * @param messageText
     * @throws Exception
     */
    private void sendMessageStorage(Object object, EventMessage message, String completeTopic, String messageText) throws Exception {
        if (sysConfig.isStoreMessage()) {
            if (eventMessageStore == null) {
                throw new Exception("您未配置messageStore!");
            }
            if (verifyStore(object, message)) {
                eventMessageStore.store(completeTopic, message.retries(), message.type(), message.ackType(), messageText);
            }
        } else {
            throw new Exception("需要开启消息存储模式,并配置messageStore后才能使用次方法");
        }
    }


    /**
     * 判断是否满足存储
     *
     * @param message 注解消息
     * @return
     */
    private boolean verifyStore(Object object, EventMessage message) throws Exception {
        Class globalClazz = BaseGlobalEntity.class;
        if (!globalClazz.isAssignableFrom(object.getClass())) {
            throw new Exception("开启存储,消息体必须继承BaseGlobalEntity类");
        }
        if (message.type().equals(KafkaMessageType.RECPRD)) {
            return true;
        } else {
            throw new Exception("存储发送,@EventMessage需要指定type!");
        }
    }


}
