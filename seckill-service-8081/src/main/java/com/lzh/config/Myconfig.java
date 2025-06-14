package com.lzh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
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
                        .title("Seckill API")
                        .contact(new Contact()
                                        .name("HackerLZH")
                                        .email("1064433607@qq.com")
                                        .url("https://github.com/HackerLZH"))
                        .description("秒杀服务")
                        .version("v1"));
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
