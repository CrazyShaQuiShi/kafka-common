package cn.shazhengbo.kafka.annotation;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author CrazyShaQiuShi
 * @version 1.0
 * @description 全局消息提
 * @date 2020/8/22 9:41
 */
@Getter
@Setter
public abstract class BaseGlobalEntity {
    public static final String GLOBAL_ID = "uuid_";
    public static final String CONSUMER_GROUP = "consumerGroup_";
    /**
     * 唯一标识
     */
    private String uuid_ = UUID.randomUUID().toString();
    /**
     * 组消息
     */
    private String consumerGroup_;
    /**
     * 消息产生时间
     */
    private LocalDateTime createdTime_ = LocalDateTime.now();
}
