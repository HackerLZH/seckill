package com.lzh.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付响应实体
 */
@Data
@Schema(description = "支付响应结果")
public class PaymentResponse implements Serializable {

    @Schema(description = "是否成功")
    private boolean success;

    @Schema(description = "响应码")
    private String code;

    @Schema(description = "响应消息")
    private String message;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "二维码链接（支付宝/微信Native支付）")
    private String qrCodeUrl;

    @Schema(description = "签名数据（前端调起支付所需参数）")
    private Object signData;

    public static PaymentResponse success(String payType, String qrCodeUrl, Object signData, Long orderId) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setCode("200");
        response.setMessage("下单成功");
        response.setPayType(payType);
        response.setQrCodeUrl(qrCodeUrl);
        response.setSignData(signData);
        response.setOrderId(orderId);
        return response;
    }

    public static PaymentResponse fail(String code, String message) {
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}