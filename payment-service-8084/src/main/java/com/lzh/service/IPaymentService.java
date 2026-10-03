package com.lzh.service;

import com.lzh.entity.PaymentRequest;
import com.lzh.entity.PaymentResponse;

import java.util.Map;

/**
 * 支付服务接口
 */
public interface IPaymentService {

    /**
     * 统一下单
     */
    PaymentResponse pay(PaymentRequest request);

    /**
     * 微信支付回调
     * @param xmlBody 微信回调XML原始报文
     */
    String wechatCallback(String xmlBody);

    /**
     * 支付宝回调
     * @param params 支付宝回调form-urlencoded参数
     */
    String alipayCallback(Map<String, String> params);

    /**
     * 查询支付状态
     */
    PaymentResponse queryStatus(String payType, Long orderId);

    /**
     * 申请退款
     */
    PaymentResponse refund(String payType, Long orderId, Integer amount, String reason);
}