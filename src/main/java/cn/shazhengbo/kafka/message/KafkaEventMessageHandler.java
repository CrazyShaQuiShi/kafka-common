package cn.shazhengbo.kafka.message;

/**
 * @author CrazyShaQiuShi
 * @description 自定义事件处理器
 * @date 2020/8/22 9:44
 * @version 1.0
 */
@FunctionalInterface
public interface KafkaEventMessageHandler<T> {

    /**
     * 处理消息
     * @param message 消息体
     */
    void handle(T message) throws Throwable;

}
