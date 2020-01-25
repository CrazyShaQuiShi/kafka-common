package cn.shazhengbo.kafka.event.listener;

import cn.shazhengbo.kafka.message.EventMessageHandler;
import cn.shazhengbo.kafka.utils.json.JsonHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

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
    private EventMessageHandler<T> handler;

    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        T eventMessage = JsonHelper.resolve(consumerRecord.value(), event);
        try {
            handler.handle(eventMessage);
            /**
             * 提交offset
             */
            acknowledgment.acknowledge();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}
