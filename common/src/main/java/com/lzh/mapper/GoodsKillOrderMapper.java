package com.lzh.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.lzh.entity.GoodsKillOrder;

@Mapper
public interface GoodsKillOrderMapper {
    @Insert("insert into goods_kill_order(`order_id`, `user_id`, `kill_id`, `create_time`) "
                + "values(#{orderId}, #{userId}, #{goodsKillId}, now())")
    int insert(GoodsKillOrder order);
}
