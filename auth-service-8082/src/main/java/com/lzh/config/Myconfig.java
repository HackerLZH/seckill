package com.lzh.config;

import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import com.lzh.entity.NacosApidocConfig;
import com.lzh.utils.Constants;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// TODO: 动态注入knife4j的配置
@Configuration
public class Myconfig {
    @Autowired
    private NacosApidocConfig config;

    /**
     * API文档
     * @return
     */
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(config.getTitle())
                        .contact(new Contact()
                                        .name(config.getAuthor())
                                        .email(config.getEmail())
                                        .url(config.getUrl()))
                        .description(config.getDescription())
                        .version(config.getVersion()))
                .components(new Components().addSecuritySchemes(
                                Constants.TOKEN_HEADER,
                                new SecurityScheme()
                                        .name(Constants.TOKEN_HEADER)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .in(SecurityScheme.In.HEADER)
                                        .bearerFormat("JWT")
                        )
                );
    }

    /**
     * 全局自定义扩展
     * <p>
     * 在OpenAPI规范中，Operation 是一个表示 API 端点（Endpoint）或操作的对象。
     * 每个路径（Path）对象可以包含一个或多个 Operation 对象，用于描述与该路径相关联的不同 HTTP 方法（例如 GET、POST、PUT 等）。
     */
    @Bean
    GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 全局添加鉴权参数
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    // 设置不加鉴权头的接口
                    if (config.getOpenapis().contains(s)) {
                        return;
                    }
                    // 接口添加鉴权参数
                    pathItem.readOperations()
                            .forEach(operation ->
                                    operation.addSecurityItem(new SecurityRequirement().addList(Constants.TOKEN_HEADER))
                            );
                });
            }
        };
    }
}
