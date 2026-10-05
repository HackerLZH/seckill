package com.lzh.entity;

import com.lzh.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsKillOrder implements Serializable{
    private Long orderId;
    private Integer userId;
    private Integer goodsKillId;
    private String qrcodeurl;
    private OrderStatus status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}
