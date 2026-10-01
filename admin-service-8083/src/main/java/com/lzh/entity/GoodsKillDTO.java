package com.lzh.entity;

import com.lzh.enums.ActiveStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoodsKillDTO {
    private Integer id; // kill id
    private Integer goodsId;
    private Integer stock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private ActiveStatus isActive;
}
