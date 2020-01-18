package cn.shazhengbo.kafka.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "sys")
public class SysConfig {
    /**
     * 主题前缀
     */
    private String topicPrefix="dev";
    /**
     * 是否为master
     */
    private boolean master;
    /**
     * 设备名称
     */
    private String serverName;
    /**
     * url地址
     */
    private String address;
}
