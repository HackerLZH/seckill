package com.lzh.filter;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.lzh.utils.Constants;
import com.lzh.utils.JwtUtil;
import com.lzh.utils.RedisUtil;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 自定义过滤器工厂：权限认证
 */
@Slf4j
@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config>{
    @Autowired
    private RedisUtil redisUtil;
    
    @NacosConfig(dataId = "common.yml", group = "EXT_GROUP", key = "token.timeout")
    private Integer tokenTimeout;


    public JwtAuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
	public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 路径在白名单中，放行
            String path = exchange.getRequest().getPath().value();
            if (config.getExcludePath().stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }
            // token是否有效
            String token = exchange.getRequest().getHeaders().getFirst(Constants.TOKEN_HEADER);
            log.info(token);
            if (Objects.isNull(token) || !redisUtil.exists(Constants.TOKEN_KEY + token)) {
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            // 校验token
            if (!JwtUtil.verify(token)) {
                // 校验失败
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.BAD_REQUEST);
                return exchange.getResponse().setComplete();
            }
            // 刷新token，重置有效期
            redisUtil.expire(Constants.TOKEN_KEY + token, tokenTimeout);
            // token传递到下游微服务
            return chain.filter(exchange);
        };
    }

    /**
     * 设置参数字段，在yml中通过args传入
     */
    @Data
    public static class Config {
        private List<String> excludePath;
	}
}
