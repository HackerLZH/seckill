package com.lzh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface GoodsKillMapper {
    /**
     * 查询商品id
     * @param killId
     * @return
     */
    @Select("select goods_id from goods_kill where id = #{killId}")
    int findgoodsId(int killId);

    /**
     * 库存扣减
     * @param id 秒杀id
     * @return
     */
    @Update("update goods_kill set stock = stock - 1 " 
            + "where id = #{id} and stock > 0")
    int cutStock(@Param("id") int id);
}
