package com.lzh.mapper;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.entity.GoodsKillOrderVO;
import com.lzh.enums.OrderStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GoodsKillOrderMapper {
    @Insert("insert into goods_kill_order(`order_id`, `user_id`, `kill_id`, `create_time`) "
                + "values(#{orderId}, #{userId}, #{goodsKillId}, now())")
    void insert(GoodsKillOrder order);

    @Update("update goods_kill_order set qrcodeurl = #{qrcodeurl} where order_id = #{orderId}")
    void updateQrCode(GoodsKillOrder order);

    @Update("update goods_kill_order set pay_time = now() where order_id = #{orderId}")
    void updatePayTime(@Param("orderId") Long orderId);

    @Select("select status from goods_kill_order where order_id = #{orderId}")
    OrderStatus selectStatus(Long orderId);

    @Update("update goods_kill_order set status = #{status} where order_id = #{orderId}")
    void updateStatus(@Param("orderId") Long orderId, @Param("status") OrderStatus status);

    @Select("select order_id, name, price, image, qrcodeurl, status, gko.create_time, gko.pay_time " +
            "from goods_kill_order gko " +
            "inner join goods_kill gk on gko.kill_id = gk.id " +
            "inner join goods g on g.id = gk.goods_id " +
            "where user_id = #{user_id} " +
            "order by gko.create_time desc")
    List<GoodsKillOrderVO> findOrdersByUserId(@Param("user_id") Integer id);

    @Select("select * from goods_kill_order where order_id = #{orderId}")
    @Results({
            @Result(column = "kill_id", property = "goodsKillId")
    })
    GoodsKillOrder findKillOrderByOrderId(@Param("orderId") Long orderId);
}
