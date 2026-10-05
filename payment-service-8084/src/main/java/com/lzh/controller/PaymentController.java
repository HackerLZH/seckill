package com.lzh.controller;

import com.lzh.entity.PaymentRequest;
import com.lzh.entity.PaymentResponse;
import com.lzh.service.IPaymentService;
import com.lzh.utils.UserHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * 支付控制器
 * 提供统一下单、回调、查询、退款等接口
 */
@Slf4j
@Tag(name = "支付接口")
@RestController
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @GetMapping("/test")
    public Map<String, Object> test() {
        return Collections.singletonMap("test", "test");
    }

    @Operation(summary = "统一下单", description = "发起支付，返回前端调起支付所需参数")
    @PostMapping("/unified")
    public PaymentResponse unifiedPay(@RequestBody PaymentRequest request) {
        // 从token解析用户ID（如果request中没有）
        if (request.getUserId() == null) {
             request.setUserId(UserHolder.getUser().getId());
        }

        return paymentService.pay(request);
    }

    @Operation(summary = "微信支付回调", description = "微信支付异步通知，接收XML格式数据")
    @PostMapping("/wechat/notify")
    public String wechatCallback(@RequestBody String xmlBody) {
        return paymentService.wechatCallback(xmlBody);
    }

    @Operation(summary = "支付宝支付回调", description = "支付宝支付异步通知，接收form-urlencoded格式数据")
    @PostMapping("/alipay/notify")
    public String alipayCallback(@RequestParam Map<String, String> params) {
        return paymentService.alipayCallback(params);
    }

    @Operation(summary = "查询支付状态", description = "根据订单ID查询支付状态")
    @GetMapping("/status/{payType}/{orderId}")
    public PaymentResponse queryStatus(
            @PathVariable("payType") String payType,
            @PathVariable("orderId") Long orderId) {

        return paymentService.queryStatus(payType, orderId);
    }

    @Operation(summary = "申请退款", description = "发起退款申请")
    @PostMapping("/refund")
    public PaymentResponse refund(
            @RequestParam String payType,
            @RequestParam Long orderId,
            @RequestParam Integer amount,
            @RequestParam(required = false) String reason) {

        return paymentService.refund(payType, orderId, amount, reason);
    }

    @Operation(summary = "获取支持的支付方式", description = "返回系统支持的所有支付方式")
    @GetMapping("/methods")
    public Map<String, Object> getSupportedMethods() {
        return Map.of(
            "methods", java.util.List.of(
                Map.of("code", "WECHAT", "name", "微信支付", "description", "支持Native扫码、JSAPI、APP、小程序"),
                Map.of("code", "ALIPAY", "name", "支付宝支付", "description", "支持网页、扫码、APP、当面付")
            )
        );
    }
}