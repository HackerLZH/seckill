package com.lzh.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.enums.OrderStatus;

@Mapper
public interface GoodsKillOrderMapper {
    @Insert("insert into goods_kill_order(`order_id`, `user_id`, `kill_id`, `create_time`) "
                + "values(#{orderId}, #{userId}, #{goodsKillId}, now())")
    void insert(GoodsKillOrder order);

    @Select("select status from goods_kill_order where order_id = #{orderId}")
    OrderStatus selectStatus(Long orderId);

    @Update("update goods_kill_order set status = #{status} where order_id = #{orderId}")
    void updateStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);
}
