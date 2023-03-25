package cn.shazhengbo.kafka.store;

import lombok.Getter;
import lombok.Setter;

/**
 * @author CrazyShaQiuShi
 * @version 1.0
 * @description 重试消息体
 * @date 2020/8/22 11:03
 */
@Getter
@Setter
public class RetryMessage {
    private String topic;
    private String eventMessage;
    private String uuid_;
}
