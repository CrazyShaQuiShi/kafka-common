package cn.shazhengbo.kafka.message;

/**
 * 自定义事件处理器
 */
@FunctionalInterface
public interface EventMessageHandler<T> {

    /**
     * 处理消息
     * @param message 消息体
     */
    void handle(T message) throws Throwable;

}
