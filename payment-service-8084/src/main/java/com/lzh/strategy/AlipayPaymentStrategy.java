package com.lzh.strategy;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.utils.qrcode.MatrixToImageWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.lzh.entity.PaymentRequest;
import com.lzh.entity.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付策略实现
 * 使用支付宝沙箱环境配置
 */
@Slf4j
@Component
public class AlipayPaymentStrategy implements PaymentStrategy {
    @NacosConfig(dataId = "payment-service.yml", group = "DEFAULT_GROUP", key = "alipay.app-id")
    private String appId;

    @NacosConfig(dataId = "payment-service.yml", group = "DEFAULT_GROUP", key = "alipay.private-key")
    private String privateKey;

    @NacosConfig(dataId = "payment-service.yml", group = "DEFAULT_GROUP", key = "alipay.public-key")
    private String publicKey;

    @NacosConfig(dataId = "payment-service.yml", group = "DEFAULT_GROUP", key = "alipay.notify-url")
    private String notifyUrl;

    @NacosConfig(dataId = "payment-service.yml", group = "DEFAULT_GROUP", key = "alipay.gateway-url")
    private String gatewayUrl;

    @NacosConfig(dataId = "payment-service.yml", group = "DEFAULT_GROUP", key = "payment.timeout")
    private String timeout;

    private volatile AlipayClient alipayClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getPayType() {
        return "ALIPAY";
    }

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        log.info("发起支付宝支付，订单号: {}, 金额: {}元", request.getOrderId(), request.getAmount());

        try {
            if (alipayClient == null) {
                synchronized (this) {
                    if (alipayClient == null) {
                        alipayClient = new DefaultAlipayClient(
                                gatewayUrl,
                                appId,
                                privateKey,
                                "json",
                                "UTF-8",
                                publicKey,
                                "RSA2"
                        );
                        log.info("支付宝沙箱环境初始化完成，APP_ID: {}", appId);
                    }
                }
            }
            // 创建预下单请求
            AlipayTradePrecreateRequest precreateRequest = new AlipayTradePrecreateRequest();
            precreateRequest.setNotifyUrl(notifyUrl);

            // 构建业务参数
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(request.getOrderId().toString()); // 订单号
            model.setTotalAmount(request.getAmount().toString());
            model.setSubject(request.getBody() != null ? request.getBody() : "秒杀商品");
            model.setTimeoutExpress(timeout); // 订单超时时间

            precreateRequest.setBizModel(model);

            // 调用支付宝预下单接口
            AlipayTradePrecreateResponse response = alipayClient.execute(precreateRequest);

            if (response.isSuccess()) {
                log.info("支付宝预下单成功，订单号: {}, 二维码链接: {}",
                    response.getOutTradeNo(), response.getQrCode());

                BitMatrix matrix = new MultiFormatWriter().encode(response.getQrCode(), BarcodeFormat.QR_CODE, 300, 300);

                // TODO 保存二维码图片（测试）
                File file = new File("/tmp/qr-code.png");
                MatrixToImageWriter.writeToFile(matrix, "PNG", file);
                log.info("二维码已生成并保存到: {}", file.getAbsolutePath());

                return PaymentResponse.success(
                    getPayType(),
                    response.getQrCode(),
                    null,
                    Long.parseLong(response.getOutTradeNo())
                );
            } else {
                log.error("支付宝支付下单失败，错误码: {}, 错误信息: {}",
                    response.getCode(), response.getMsg());
                return PaymentResponse.fail(
                    response.getSubCode() != null ? response.getSubCode() : response.getCode(),
                    response.getSubMsg() != null ? response.getSubMsg() : response.getMsg()
                );
            }

        } catch (AlipayApiException e) {
            log.error("支付宝API调用异常，订单号: {}", request.getOrderId(), e);
            return PaymentResponse.fail("ALIPAY_API_ERROR", "支付宝API异常: " + e.getMessage());
        } catch (Exception e) {
            log.error("支付宝支付下单失败，订单号: {}", request.getOrderId(), e);
            return PaymentResponse.fail("ALIPAY_PAY_ERROR", "支付宝支付下单失败: " + e.getMessage());
        }
    }

    @Override
    public boolean handleCallback(Map<String, String> params) {
        try {
            // 1. 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(
                params,
                publicKey,
                "UTF-8",
                "RSA2"
            );

            if (!signVerified) {
                log.error("支付宝回调签名验证失败");
                return false;
            }

            // 2. 解析回调参数
            String tradeNo = params.get("trade_no");
            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            String buyerId = params.get("buyer_id");

            // 3. 处理业务逻辑
            // TRADE_SUCCESS: 支付成功
            // TRADE_FINISHED: 交易完结
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                log.info("支付宝支付成功，交易号: {}, 商户订单号: {}, 买家ID: {}",
                    tradeNo, outTradeNo, buyerId);

                return true;
            }

            return false;

        } catch (AlipayApiException e) {
            log.error("支付宝回调处理失败", e);
            return false;
        } catch (Exception e) {
            log.error("支付宝回调处理异常", e);
            return false;
        }
    }

    @Override
    public PaymentResponse queryStatus(Long orderId) {
        log.info("查询支付宝支付状态，订单号: {}", orderId);

        try {
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(String.valueOf(orderId));
            queryRequest.setBizModel(model);

            AlipayTradeQueryResponse response = alipayClient.execute(queryRequest);

            if (response.isSuccess()) {
                log.info("支付宝订单查询成功，交易状态: {}", response.getTradeStatus());

                PaymentResponse paymentResponse = new PaymentResponse();
                paymentResponse.setSuccess(true);
                paymentResponse.setCode("200");
                paymentResponse.setMessage(response.getTradeStatus());
                paymentResponse.setPayType(getPayType());
                paymentResponse.setOrderId(orderId);

                return paymentResponse;
            } else {
                log.error("支付宝订单查询失败，错误码: {}, 错误信息: {}",
                    response.getCode(), response.getMsg());
                return PaymentResponse.fail(response.getCode(), response.getMsg());
            }

        } catch (AlipayApiException e) {
            log.error("查询支付宝支付状态异常", e);
            return PaymentResponse.fail("QUERY_ERROR", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse refund(Long orderId, Integer amount, String reason) {
        log.info("申请支付宝退款，订单号: {}, 金额: {}分, 原因: {}", orderId, amount, reason);

        try {
            AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(String.valueOf(orderId));
            // 金额从分转换为元
            model.setRefundAmount(new BigDecimal(amount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString());
            model.setRefundReason(reason != null ? reason : "用户申请退款");
            refundRequest.setBizModel(model);

            AlipayTradeRefundResponse response = alipayClient.execute(refundRequest);

            if (response.isSuccess()) {
                log.info("支付宝退款成功，退款金额: {}元", response.getRefundFee());

                return PaymentResponse.success(
                    getPayType(),
                    null,
                    null,
                    orderId
                );
            } else {
                log.error("支付宝退款失败，错误码: {}, 错误信息: {}",
                    response.getCode(), response.getMsg());
                return PaymentResponse.fail(response.getCode(), response.getMsg());
            }

        } catch (AlipayApiException e) {
            log.error("支付宝退款申请异常", e);
            return PaymentResponse.fail("REFUND_ERROR", "退款失败: " + e.getMessage());
        }
    }
}