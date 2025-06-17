package com.lzh.service;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.response.Result;

public interface ISeckillService {
    /**
     * 秒杀
     * @param killId
     * @return
     */
    Result kill(Integer killId);
    /**
     * 下订单
     * @param order
     */
    void saveOrder(GoodsKillOrder order);
    /**
     * 处理超时订单
     * @param order
     */
    void processTimeOutOrder(GoodsKillOrder order);
}
