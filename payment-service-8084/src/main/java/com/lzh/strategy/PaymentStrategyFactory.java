package com.lzh.strategy;

import com.lzh.entity.PaymentRequest;
import com.lzh.entity.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付策略工厂
 * 管理和分发不同的支付策略，实现支付方式的可扩展性
 */
@Slf4j
@Component
public class PaymentStrategyFactory {

    @Autowired
    private WechatPaymentStrategy wechatPaymentStrategy;

    @Autowired
    private AlipayPaymentStrategy alipayPaymentStrategy;

    private final Map<String, PaymentStrategy> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 注册所有支付策略
        registerStrategy(wechatPaymentStrategy);
        registerStrategy(alipayPaymentStrategy);

        log.info("支付策略工厂初始化完成，已注册支付方式: {}", strategyMap.keySet());
    }

    /**
     * 注册支付策略
     * 扩展新支付方式只需实现 PaymentStrategy 接口并在此处注册
     */
    private void registerStrategy(PaymentStrategy strategy) {
        strategyMap.put(strategy.getPayType().toUpperCase(), strategy);
    }

    /**
     * 根据支付方式获取对应的策略
     */
    public PaymentStrategy getStrategy(String payType) {
        if (payType == null) {
            throw new IllegalArgumentException("支付方式不能为空");
        }
        PaymentStrategy strategy = strategyMap.get(payType.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的支付方式: " + payType +
                "，当前支持的支付方式: " + strategyMap.keySet());
        }
        return strategy;
    }

    /**
     * 统一下单入口
     */
    public PaymentResponse pay(PaymentRequest request) {
        PaymentStrategy strategy = getStrategy(request.getPayType());
        return strategy.pay(request);
    }

    /**
     * 统一回调处理入口
     */
    public boolean handleCallback(String payType, Map<String, String> params) {
        PaymentStrategy strategy = getStrategy(payType);
        return strategy.handleCallback(params);
    }

    /**
     * 统一查询入口
     */
    public PaymentResponse queryStatus(String payType, Long orderId) {
        PaymentStrategy strategy = getStrategy(payType);
        return strategy.queryStatus(orderId);
    }

    /**
     * 统一退款入口
     */
    public PaymentResponse refund(String payType, Long orderId, Integer amount, String reason) {
        PaymentStrategy strategy = getStrategy(payType);
        return strategy.refund(orderId, amount, reason);
    }

    /**
     * 获取所有支持的支付方式
     */
    public java.util.Set<String> getSupportedPayTypes() {
        return strategyMap.keySet();
    }
}