package com.lzh.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "seckill-service", path = "seckill", configuration = ResponseDecoder.class)
public interface SeckillFeign {
    @GetMapping("/internal/check/order")
    boolean checkOrder(@RequestParam("orderId") Long orderId, @RequestParam("userId") Integer userId);

    @PostMapping("/internal/postprocess")
    void postprocess(@RequestParam("orderId") Long orderId);
}
