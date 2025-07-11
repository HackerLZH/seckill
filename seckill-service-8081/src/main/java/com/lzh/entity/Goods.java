package com.lzh.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.lzh.enums.ActiveStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Goods {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime createTime;
    private ActiveStatus isActive;
}
