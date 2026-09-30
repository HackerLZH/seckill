package com.lzh.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.lzh.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsKillOrder implements Serializable{
    private Long orderId;
    private Integer userId;
    private Integer goodsKillId;
    private OrderStatus status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
}
