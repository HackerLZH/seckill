package com.lzh.entity;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoodsKillOrder implements Serializable{
    private Long orderId;
    private Integer userId;
    private Integer goodsKillId;
    private Integer status;
}
