package com.lzh.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        private String image;
        private BigDecimal price;
        private LocalDateTime startTime;
        private Integer stock; //秒杀库存
        private Integer availableStock; // redis获取
    } 
}
