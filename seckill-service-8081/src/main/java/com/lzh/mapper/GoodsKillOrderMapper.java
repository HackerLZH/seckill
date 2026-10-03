package com.lzh.mapper;

import com.lzh.entity.GoodsKillOrderVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.enums.OrderStatus;

import java.util.List;

@Mapper
public interface GoodsKillOrderMapper {
    @Insert("insert into goods_kill_order(`order_id`, `user_id`, `kill_id`, `create_time`) "
                + "values(#{orderId}, #{userId}, #{goodsKillId}, now())")
    void insert(GoodsKillOrder order);

    @Select("select status from goods_kill_order where order_id = #{orderId}")
    OrderStatus selectStatus(Long orderId);

    @Update("update goods_kill_order set status = #{status} where order_id = #{orderId}")
    void updateStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);

    @Select("select order_id, name, price, image, status, gko.create_time " +
            "from goods_kill_order gko " +
            "inner join goods_kill gk on gko.kill_id = gk.id " +
            "inner join goods g on g.id = gk.goods_id " +
            "where user_id = #{user_id} " +
            "order by gko.create_time desc")
    List<GoodsKillOrderVO> findOrdersByUserId(@Param("user_id") Integer id);

    @Select("select * from goods_kill_order where order_id = #{orderId}")
    GoodsKillOrder findKillOrderByOrderId(@Param("orderId") Long orderId);
}
