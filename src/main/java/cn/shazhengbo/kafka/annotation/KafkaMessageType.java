package cn.shazhengbo.kafka.annotation;

/**
 * @author CrazyShaQiuShi
 * @version 1.0
 * @description 消息类型
 * @date 2020/8/22 8:59
 */
public enum KafkaMessageType {
    /**
     * 无记录
     */
    NO_RECORD,
    /**
     * 有记录,无确认
     */
    RECPRD;


}
