package com.lzh.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", path = "auth", fallbackFactory = AuthFeignFallbackFactory.class)
public interface AuthFeign {
    @PostMapping("/internal/login")
    void login(@RequestParam("username") String username, @RequestParam("password") String password);
}
