package com.lzh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;

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
                // .components(new Components().addSecuritySchemes("basicScheme", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"))
                // .addParameters("myHeader1"
                //     , new Parameter()
                //         .in("header")
                //         .schema(new StringSchema())
                //         .name("myHeader1"))
                //         .addHeaders("myHeader2"
                //             , new Header().description("myHeader2 header")
                //                 .schema(new StringSchema())))
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
