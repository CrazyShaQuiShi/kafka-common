package cn.shazhengbo.kafka.annotation;

/**
 * @author CrazyShaQiuShi
 * @description:发送状态
 * @date 2020/8/22 20:13
 * @version 1.0
 */
public enum  KafkaMessageState {
    /**
     * 准备发送
     */
    READY,
    /**
     * 失败
     */
    FAIL,
    /**
     * 成功
     */
    SUCCESS;
}
