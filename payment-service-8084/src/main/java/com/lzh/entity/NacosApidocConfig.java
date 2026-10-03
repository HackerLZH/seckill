package com.lzh.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 在nacos中配置的与knife4j文档相关的一些配置
 */
@Component
@ConfigurationProperties(prefix = "apidoc")
@Data
public class NacosApidocConfig {
    private String title;
    private String author;
    private String url;
    private String email;
    private String version;
    private String description;
    private Set<String> openapis;
}
