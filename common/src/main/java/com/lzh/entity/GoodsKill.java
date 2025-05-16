package com.lzh.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoodsKill {
    private Integer id;
    private Integer goodsId;
    private Integer stock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private Integer isActive;
}
