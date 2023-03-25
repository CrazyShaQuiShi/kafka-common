package cn.shazhengbo.kafka.annotation;

/**
 * @author CrazyShaQiuShi
 * @version 1.0
 * @description:确认方式
 * @date 2020/8/22 9:54
 */
public enum KafkaAckType {
    /**
     * 不需要确认
     */
    NONE,
    /**
     * 自动确认
     */
    AUTO,
    /**
     * 网略确认(未实现)
     */
    RPC;
}
