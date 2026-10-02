package com.lzh.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "auth-service", path = "auth")
public interface AuthFeign {
    @PostMapping("/login2")
    void login(@RequestParam("username") String username, @RequestParam("password") String password);
}
