package com.lzh.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付请求实体
 */
@Data
@Schema(description = "支付请求参数")
public class PaymentRequest implements Serializable {

    @Schema(description = "订单ID", required = true)
    private Long orderId;

    @Schema(description = "支付方式：WECHAT/ALIPAY", required = true, allowableValues = {"WECHAT", "ALIPAY"})
    private String payType;

    @Schema(description = "用户ID（从token中解析）")
    private Integer userId;

    @Schema(description = "支付金额（元）")
    private BigDecimal amount;

    @Schema(description = "商品描述")
    private String body;

    @Schema(description = "客户端IP")
    private String clientIp;
}