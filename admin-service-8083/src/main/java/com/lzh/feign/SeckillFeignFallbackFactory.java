package com.lzh.feign;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * SeckillFeign的fallback工厂
 */
@Slf4j
@Component
public class SeckillFeignFallbackFactory implements FallbackFactory<SeckillFeign> {
    @Override
    public SeckillFeign create(Throwable throwable) {
        if (throwable instanceof FeignException) {
            log.warn("[Feign] 调用失败: {} - {}", throwable.getClass().getSimpleName(), throwable.getMessage());
        } else {
            log.warn("[Sentinel] 降级原因: {} - {}", throwable.getClass().getSimpleName(), throwable.getMessage());
        }
        return new SeckillFeign() {
            @Override
            public void evictKillCache() {
                log.warn("[Fallback] SeckillFeign.evictKillCache 降级");
                // 无需抛异常，直接返回即可
            }
        };
    }
}