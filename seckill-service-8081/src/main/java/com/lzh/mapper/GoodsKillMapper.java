package com.lzh.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.lzh.entity.GoodsKillVO.GoodsKillInfo;

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

    /**
     * 查询当前秒杀商品
     * @return
     */
    @Select("select g1.id, g1.name, g1.price, g2.stock "
            + "from goods g1 "
            + "inner join goods_kill g2 "
            + "on g1.id = g2.goods_id "
            + "where now() >= g2.start_time")
    List<GoodsKillInfo> selectCurrent();

    /**
     * 查询即将秒杀商品
     * @return
     */
    @Select("select g1.name, g1.price, g2.start_time "
        + "from goods g1 "
        + "inner join goods_kill g2 "
        + "on g1.id = g2.goods_id "
        + "where now() < g2.start_time")
    List<GoodsKillInfo> selectUpcoming();
}
