package com.lzh.entity;

import java.time.LocalDateTime;

import com.lzh.enums.ActiveStatus;

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
    private ActiveStatus isActive;
}
