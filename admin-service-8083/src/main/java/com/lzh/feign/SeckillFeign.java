package com.lzh.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "seckill-service", path = "seckill")
public interface SeckillFeign {
    @PostMapping("/internal/evict/kill/cache")
    void evictKillCache();
}
