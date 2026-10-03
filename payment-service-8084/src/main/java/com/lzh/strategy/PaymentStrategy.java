package com.lzh.strategy;

import com.lzh.entity.PaymentRequest;
import com.lzh.entity.PaymentResponse;

/**
 * 支付策略接口
 * 实现支付方式的可扩展性
 */
public interface PaymentStrategy {

    /**
     * 获取支付方式类型
     * @return 支付方式代码，如 WECHAT, ALIPAY
     */
    String getPayType();

    /**
     * 统一下单/发起支付
     * @param request 支付请求参数
     * @return 支付响应
     */
    PaymentResponse pay(PaymentRequest request);

    /**
     * 支付回调处理
     * @param params 回调参数
     * @return 处理结果
     */
    boolean handleCallback(java.util.Map<String, String> params);

    /**
     * 查询支付状态
     * @param orderId 订单ID
     * @return 支付状态
     */
    PaymentResponse queryStatus(Long orderId);

    /**
     * 申请退款
     * @param orderId 订单ID
     * @param amount 退款金额
     * @param reason 退款原因
     * @return 退款结果
     */
    PaymentResponse refund(Long orderId, Integer amount, String reason);
}