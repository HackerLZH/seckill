package com.lzh.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private Integer status;
    private LocalDateTime createTime;
}
