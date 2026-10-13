package com.lzh.feign;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * AuthFeign的fallback工厂
 */
@Slf4j
@Component
public class AuthFeignFallbackFactory implements FallbackFactory<AuthFeign> {
    @Override
    public AuthFeign create(Throwable throwable) {
        if (throwable instanceof FeignException) {
            log.warn("[Feign] 调用失败: {} - {}", throwable.getClass().getSimpleName(), throwable.getMessage());
        } else {
            log.warn("[Sentinel] 降级原因: {} - {}", throwable.getClass().getSimpleName(), throwable.getMessage());
        }
        return new AuthFeign() {
            @Override
            public void login(String username, String password) {
                // 登录失败时，不抛异常，只记录日志。业务层应根据实际情况处理（例如返回登录失败响应）。
                log.warn("[Fallback] AuthFeign.login 降级，username={}, password={}", username, password);
            }
        };
    }
}