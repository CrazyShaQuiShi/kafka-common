package cn.shazhengbo.kafka.event.subscriber;

import cn.shazhengbo.kafka.annotation.EventMessage;
import cn.shazhengbo.kafka.annotation.EventMessageListener;
import cn.shazhengbo.kafka.config.SysConfig;
import cn.shazhengbo.kafka.event.ServiceHelper;
import cn.shazhengbo.kafka.event.listener.EventKafkaEventListener;
import cn.shazhengbo.kafka.message.KafkaEventMessageHandler;
import cn.shazhengbo.kafka.utils.aop.AopTargetUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: crazyshaqiushi
 * @create_time: 2019/12/11-9:39
 * @description: 消息订阅类
 * @version:1.0.0
 */
@Slf4j
@Component
public class EventSubscriber implements ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext applicationContext;

    private final SysConfig sysConfig;
    private final ConsumerFactory consumerFactory;
    private final ConcurrentMap<String, ConcurrentMessageListenerContainer<String, String>> consumers = new ConcurrentHashMap<>();

    @Autowired
    public EventSubscriber(SysConfig sysConfig, ConsumerFactory consumerFactory) {
        this.sysConfig = sysConfig;
        this.consumerFactory = consumerFactory;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        applicationContext = event.getApplicationContext();
        /**
         * 获取spring容器中所有实现了EventMessageHandler接口的Bean,并注册相应事件的监听器
         * {@link cn.shazhengbo.kafka.message.EventMessageHandler}
         */
        Map<String, KafkaEventMessageHandler> handlers = event.getApplicationContext().getBeansOfType(KafkaEventMessageHandler.class);
        init(handlers.values());
    }

    /**
     * 初始化数据
     *
     * @param handlers
     */
    private void init(Collection<KafkaEventMessageHandler> handlers) {
        handlers.forEach(h -> {
            KafkaEventMessageHandler originalHandler = AopTargetUtils.getTarget(h);
            log.info(String.format("开始注册消息处理器：%s", originalHandler.getClass().getName()));
            Type[] types = originalHandler.getClass().getGenericInterfaces();
            if (!types[0].getTypeName().equals(KafkaEventMessageHandler.class.getTypeName())) {
                Class<?> clazz = (Class<?>) ((ParameterizedType) types[0]).getActualTypeArguments()[0];
                EventMessageListener annotation = ServiceHelper.retrieveMessageListener(originalHandler.getClass());
                subscribe(annotation, clazz, h);
                log.info(String.format("已注册消息【%s】的处理器：%s", clazz.getName(), originalHandler.getClass().getName()));
            }
        });
    }

    /**
     * 订阅
     *
     * @param annotation
     * @param event
     * @param handler
     * @param <T>
     */
    public <T> void subscribe(EventMessageListener annotation, Class<T> event, KafkaEventMessageHandler<T> handler) {
        EventMessage crawlMessage = ServiceHelper.retrieveLeopardMessage(event);
        EventKafkaEventListener<T> eventListener = applicationContext.getBean(EventKafkaEventListener.class);
        eventListener.setHandler(handler);
        eventListener.setConsumerGroup(annotation.group());
        eventListener.setEvent(event);
        subscribe(annotation, crawlMessage.topic(), eventListener);
    }

    /**
     *  订阅实现
     * @param annotation
     * @param topic
     * @param eventListener
     * @param <T>
     */
    private <T> void subscribe(EventMessageListener annotation, String topic,Object eventListener) {
        String key = calculateHashCode(annotation.group(), topic);
        String newTopic = generateTopic(topic);
        ConcurrentMessageListenerContainer<String, String> container = ServiceHelper.createListenerContainer(annotation, newTopic, consumerFactory, eventListener);
        ConcurrentMessageListenerContainer<String, String> existContainer = consumers.putIfAbsent(key, container);
        container = existContainer == null ? container : existContainer;
        if (!container.isRunning()) {
            container.start();
        }
        log.info(String.format("已注册 %s 的消费者 %s", newTopic, annotation.group()));
    }

    /**
     * 自动组成主题信息
     *
     * @param topic
     * @return
     */
    private String generateTopic(String topic) {

        return String.format("%s.%s", sysConfig.getTopicPrefix(), topic);
    }

    /**
     * 计算hash值
     *
     * @param group
     * @param topic
     * @return
     */
    private String calculateHashCode(String group, String topic) {
        return String.format("%s.%s", group, topic);
    }

}
