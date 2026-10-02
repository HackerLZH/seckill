package com.lzh.entity;

import com.lzh.enums.ActiveStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class Goods {
    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private LocalDateTime createTime;
    private ActiveStatus isActive;
}
