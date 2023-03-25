package cn.shazhengbo.kafka.event;

import cn.shazhengbo.kafka.annotation.EventMessage;
import cn.shazhengbo.kafka.annotation.EventMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.Properties;

/**
 * @author: crazyshaqiushi
 * @create_time: 2019/12/11-9:50
 * @description:服务辅助类
 * @version:1.0.0
 */
@Slf4j
public abstract class ServiceHelper {


    /**
     * 获取消息注解
     *
     * @param clazz 消息类型
     * @param <T>   消息类型
     * @return 消息注解
     */
    public static <T> EventMessage retrieveLeopardMessage(Class<T> clazz) {
        EventMessage leopardMessage = clazz.getAnnotation(EventMessage.class);
        if (leopardMessage == null) {
            log.error(String.format("消息【%s】未进行EventMessage注解", clazz.getName()));
        }

        return leopardMessage;
    }


    public static <T> EventMessageListener retrieveMessageListener(Class<T> clazz) {
        EventMessageListener messageListener = clazz.getAnnotation(EventMessageListener.class);
        if (messageListener == null) {
            log.error(String.format("消息处理器【%s】未进行EventMessageListener注解", clazz.getName()));
        }
        return messageListener;
    }


    public static ConcurrentMessageListenerContainer<String, String> createListenerContainer(EventMessageListener annotation,String topic, ConsumerFactory consumerFactory,
                                                                                             Object messageListener) {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setGroupId(annotation.group());
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setPollTimeout(annotation.maxPollIntervalMs());
        containerProperties.setMessageListener(messageListener);
        Properties kafkaConsumerProperties = new Properties();
        kafkaConsumerProperties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, String.valueOf(annotation.maxPollRecords()));
        kafkaConsumerProperties.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, String.valueOf(annotation.maxPollIntervalMs()));
        kafkaConsumerProperties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, String.valueOf(false));
        kafkaConsumerProperties.setProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, String.valueOf(annotation.requestTimeoutMs()));
        kafkaConsumerProperties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, String.valueOf(annotation.commitIntervalMs()));
        containerProperties.setKafkaConsumerProperties(kafkaConsumerProperties);
        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<String, String>(consumerFactory, containerProperties);
        container.setAutoStartup(false);
        container.setBeanName(String.format("%s-%s", topic, annotation.group()));
        return container;
    }


}
