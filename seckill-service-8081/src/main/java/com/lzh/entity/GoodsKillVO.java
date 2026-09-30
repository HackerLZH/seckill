package com.lzh.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 秒杀商品视图
 */
@Builder
@Data
public class GoodsKillVO {
    private List<GoodsKillInfo> current;
    private List<GoodsKillInfo> upcoming;

    @Data
    public static class GoodsKillInfo { 
        private Integer id;
        private String name;
        private BigDecimal price;
        private LocalDateTime startTime;
        private Integer stock; //秒杀库存
        private Integer availableStock; // redis获取
    } 
}
