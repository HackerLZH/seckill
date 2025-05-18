package com.lzh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface GoodsMapper {
    /**
     * 库存扣减
     * @param id 商品id
     * @param num 扣减数量
     * @return
     */
    @Update("update goods set stock = stock - #{num} " 
                + "where id = #{id} and stock >= #{num}")
    int cutStock(@Param("id") int id, @Param("num") int num);
}
