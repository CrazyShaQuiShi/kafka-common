package cn.shazhengbo.kafka.store;

import cn.shazhengbo.kafka.annotation.KafkaAckType;
import cn.shazhengbo.kafka.annotation.KafkaMessageState;
import cn.shazhengbo.kafka.annotation.KafkaMessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author CrazyShaQiuShi
 * @version 1.0
 * @description 消息存储接口
 * @date 2020/8/22 9:19
 */
@Slf4j
public abstract class AbstractMessageStore {
    /**
     * 存储消息
     *
     * @param topic
     * @param retries 重试次数
     * @param message 消息体
     */
    public void store(String topic, int retries, KafkaMessageType type, KafkaAckType ackType, String message) {
        /**
         * 判读消息类型决定是否存储消息
         */
        try {
            log.debug("主题:{},重试次数:{},消息体:{}", topic, retries, message);
        } catch (Exception e) {
            log.error("记录消息失败:{}", e.getMessage());
        }
    }

    /**
     * 确认消息处理
     *
     * @param topic
     * @param uuid
     */
    public void ackDeal(String topic,long offSet, String uuid,String consumerGroup,KafkaAckType ackType) {
        log.debug("确认主题:{}--消息体uuid:{},被处理了", topic, uuid);
    }

    /**
     * 确认发送成功
     *
     * @param topic
     * @param uuid
     */
    public void sendState(String topic, String uuid, KafkaMessageState state) {
        log.debug("确认主题:{}--消息体uuid:{},被重新发送了!");
        log.debug("此时需要重新设定消息状态和下次重试的机制");
    }

    /**
     * 生成重发消息
     * @param uuid
     * @param topic
     * @param consumerGroup
     */
    public abstract void retryMessage(long offSet,String uuid, String topic, String consumerGroup,long maxAckCount);
}
