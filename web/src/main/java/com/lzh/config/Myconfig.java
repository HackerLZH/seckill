package com.lzh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class Myconfig {
    @Autowired
    private Environment env;
    /**
     * API文档
     * @return
     */
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Seckill API")
                        .description("SpringBoot3 集成 Swagger3接口文档")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("项目API文档")
                        .url("/"));
    }
    /**
     * 生成雪花id
     * @return
     */
    @Bean
    Snowflake snowflake() {
        return IdUtil.getSnowflake(1, 1);
    }

    /**
     * Redisson分布式锁
     * @return
     */
    // @Bean
    // RedissonClient redissoncClient() {
    //     Config config = new Config();
    //     config.useSingleServer()
    //             .setAddress(String.format("redis://%s:%s"
    //                 , env.getProperty("spring.data.redis.host")
    //                 , env.getProperty("spring.data.redis.port")))
    //             .setPassword(env.getProperty("spring.data.redis.password"));
    //     return Redisson.create(config);
    // }
}
