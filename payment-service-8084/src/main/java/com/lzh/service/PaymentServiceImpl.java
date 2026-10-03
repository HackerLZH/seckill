package com.lzh.service;

import com.lzh.entity.PaymentRequest;
import com.lzh.entity.PaymentResponse;
import com.lzh.strategy.PaymentStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付服务实现
 */
@Slf4j
@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentStrategyFactory paymentStrategyFactory;

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        log.info("收到下单请求：orderId={}, payType={}, amount={}",
                request.getOrderId(), request.getPayType(), request.getAmount());

        // 1. 校验参数
        if (request.getOrderId() == null) {
            return PaymentResponse.fail("PARAM_ERROR", "订单 ID 不能为空");
        }
        if (request.getPayType() == null || request.getPayType().trim().isEmpty()) {
            return PaymentResponse.fail("PARAM_ERROR", "支付方式不能为空");
        }

        // 2. 委托给策略工厂处理
        try {
            return paymentStrategyFactory.pay(request);
        } catch (IllegalArgumentException e) {
            log.warn("不支持的支付方式：{}", request.getPayType());
            return PaymentResponse.fail("UNSUPPORTED_PAY_TYPE", e.getMessage());
        } catch (Exception e) {
            log.error("支付下单异常", e);
            return PaymentResponse.fail("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }

    @Override
    public String wechatCallback(String xmlBody) {
        log.info("收到微信支付回调，XML 长度：{}", xmlBody.length());

        try {
            // 解析 XML 并提取参数
            Map<String, String> params = parseXmlToMap(xmlBody);

            boolean success = paymentStrategyFactory.handleCallback("WECHAT", params);
            if (success) {
                log.info("微信支付回调处理成功");
                return "SUCCESS"; // 告诉微信处理成功
            }
        } catch (Exception e) {
            log.error("微信支付回调处理异常", e);
        }

        return "FAIL"; // 告诉微信处理失败，微信会重试
    }

    @Override
    public String alipayCallback(Map<String, String> params) {
        log.info("收到支付宝支付回调：{}，参数数量：{}", params, params.size());

        try {
            boolean success = paymentStrategyFactory.handleCallback("ALIPAY", params);
            if (success) {
                log.info("支付宝支付回调处理成功");
                return "success"; // 告诉支付宝处理成功
            }
        } catch (Exception e) {
            log.error("支付宝支付回调处理异常", e);
        }

        return "fail"; // 告诉支付宝处理失败
    }

    @Override
    public PaymentResponse queryStatus(String payType, Long orderId) {
        log.info("查询支付状态：payType={}, orderId={}", payType, orderId);

        try {
            return paymentStrategyFactory.queryStatus(payType, orderId);
        } catch (Exception e) {
            log.error("查询支付状态异常", e);
            return PaymentResponse.fail("QUERY_ERROR", "查询失败：" + e.getMessage());
        }
    }

    @Override
    public PaymentResponse refund(String payType, Long orderId, Integer amount, String reason) {
        log.info("申请退款：payType={}, orderId={}, amount={}, reason={}", payType, orderId, amount, reason);

        try {
            return paymentStrategyFactory.refund(payType, orderId, amount, reason);
        } catch (Exception e) {
            log.error("退款异常", e);
            return PaymentResponse.fail("REFUND_ERROR", "退款失败：" + e.getMessage());
        }
    }

    /**
     * 将微信 XML 回调解析为 Map
     */
    private Map<String, String> parseXmlToMap(String xml) {
        Map<String, String> map = new java.util.HashMap<>();
        try {
            // 简单 XML 解析（生产环境建议使用专业库如 dom4j）
            javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(
                new java.io.ByteArrayInputStream(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8))
            );
            doc.getDocumentElement().normalize();

            org.w3c.dom.NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                org.w3c.dom.Node node = nodeList.item(i);
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    String key = node.getNodeName();
                    String value = node.getTextContent();
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("解析微信 XML 回调失败", e);
        }
        return map;
    }
}