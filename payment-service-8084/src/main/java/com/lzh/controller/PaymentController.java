package com.lzh.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Tag(name = "支付接口")
@RestController
public class PaymentController {
    @GetMapping("/test")
    public Map<String, Object> test() {
        return Collections.singletonMap("test", "test");
    }
}
