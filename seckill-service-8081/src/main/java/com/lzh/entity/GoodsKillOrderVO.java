package com.lzh.entity;

import com.lzh.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsKillOrderVO {
    private String orderId;
    private String name;
    private BigDecimal price;
    private String image;
    private String qrcodeurl;
    private OrderStatus status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}
