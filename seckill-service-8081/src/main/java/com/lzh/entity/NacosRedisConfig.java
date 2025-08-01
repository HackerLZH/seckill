package com.lzh.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 在nacos中配置的与knife4j文档相关的一些配置
 */
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class NacosRedisConfig {
    private String host;
    private String port;
    private String password;
}
