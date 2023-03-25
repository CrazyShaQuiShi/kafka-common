package cn.shazhengbo.kafka.store;

import java.util.List;

/**
 * @author CrazyShaQiuShi
 * @version 1.0
 * @description: 查询消息体的接口
 * @date 2020/8/22 11:02
 */
public abstract class AbstractQueryMessageStore {
    /**
     * 获取重发的前几条消息,包含存储未发送成功,需要确认重试的
     *
     * @param num
     * @return
     */
    public List<RetryMessage> topsRetryMessage(int num) {
        return null;
    }
}
