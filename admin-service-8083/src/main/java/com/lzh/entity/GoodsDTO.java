package com.lzh.entity;

import com.lzh.enums.ActiveStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsDTO {
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private LocalDateTime createTime;
    private ActiveStatus isActive;
}
