package com.lzh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsKillMapper {
    /**
     * 查询商品id
     * @param killId
     * @return
     */
    @Select("select goods_id from goods_kill where id = #{killId}")
    Integer findgoodsId(int killId);

    /**
     * 库存扣减
     * @param id 秒杀id
     * @return
     */
    @Update("update goods_kill set stock = stock - 1 " 
            + "where id = #{id} and stock > 0")
    void cutStock(@Param("id") int id);
    @Update("update goods_kill set stock = stock + #{num} where id = #{id}")
    void addStock(@Param("id") int id, @Param("num") int num);
}
