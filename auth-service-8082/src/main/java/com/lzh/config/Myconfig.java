package com.lzh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class Myconfig {
    /**
     * API文档
     * @return
     */
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth API")
                        .contact(new Contact()
                                        .name("HackerLZH")
                                        .email("1064433607@qq.com")
                                        .url("https://github.com/HackerLZH"))
                        .description("用户认证")
                        .version("v1"));
    }
}
