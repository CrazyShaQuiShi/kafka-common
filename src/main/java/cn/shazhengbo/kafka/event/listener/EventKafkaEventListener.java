package cn.shazhengbo.kafka.event.listener;

import cn.shazhengbo.kafka.annotation.EventMessage;
import cn.shazhengbo.kafka.annotation.KafkaAckType;
import cn.shazhengbo.kafka.config.SysConfig;
import cn.shazhengbo.kafka.message.KafkaEventMessageHandler;
import cn.shazhengbo.kafka.store.AbstractMessageStore;
import cn.shazhengbo.kafka.utils.json.JsonHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Map;

import static cn.shazhengbo.kafka.annotation.BaseGlobalEntity.CONSUMER_GROUP;
import static cn.shazhengbo.kafka.annotation.BaseGlobalEntity.GLOBAL_ID;

/**
 * @author: crazyshaqiushi
 * @create_time: 2019/12/11-10:12
 * @description: 事件监听处理
 * @version:1.0.0
 */
@Slf4j
@Getter
@Setter
@Service
@Scope("prototype")
public class EventKafkaEventListener<T> implements AcknowledgingMessageListener<String, String> {
    private Class<T> event;
    private String consumerGroup;

    @Getter
    @Setter
    private KafkaEventMessageHandler<T> handler;

    @Autowired(required = false)
    private AbstractMessageStore messageStore;
    @Autowired
    private SysConfig sysConfig;

    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        T eventMessage = JsonHelper.resolve(consumerRecord.value(), event);
        EventMessage message = event.getAnnotation(EventMessage.class);
        Map<String, String> map = JsonHelper.resolve(consumerRecord.value(), Map.class);
        String uuid = map.get(GLOBAL_ID);
        try {
            if (enableHandle(consumerRecord.value())) {
                handler.handle(eventMessage);
                /**
                 * 提交offset
                 */
                acknowledgment.acknowledge();
                /**
                 * 自动确认或rpc的确认机制
                 */
                if (KafkaAckType.AUTO.equals(message.ackType()) || KafkaAckType.RPC.equals(message.ackType())) {
                    ackMessage(consumerRecord.topic(),consumerRecord.offset(), uuid, message.ackType());
                } else {
                    log.debug("无需确认的消息:{},已被处理!", uuid);
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(throwable.getMessage());
            /**
             * 发生异常,若为ack消息,则生成重发消息,并绑定parentId关系
             */
            if (KafkaAckType.AUTO.equals(message.ackType())) {
                messageStore.retryMessage(consumerRecord.offset(),uuid, consumerRecord.topic(), getConsumerGroup(),message.maxAckCount());
            }
        }
    }

    /**
     * 可处理
     *
     * @param value
     * @return
     */
    private boolean enableHandle(String value) {
        Map<String, Object> map = JsonHelper.resolve(value, Map.class);
        Object group = map.get(CONSUMER_GROUP);
        if (group == null) {
            return true;
        } else if (group.toString().equals(consumerGroup)) {
            return true;
        }
        return false;
    }

    /**
     * 确认消息
     *
     * @param completeTopic
     */
    private void ackMessage(String completeTopic,long offSet, String uuid, KafkaAckType ackType) {
        if (messageStore == null) {
            log.error("您未配置messageStore消息将不会被确认!");
        } else {
            messageStore.ackDeal(completeTopic,offSet, uuid, getConsumerGroup(), ackType);
        }

    }


}
