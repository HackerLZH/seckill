package com.lzh.service;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.entity.GoodsKillVO;

public interface ISeckillService {
    /**
     * 秒杀
     * @param killId
     * @return
     */
    void kill(Integer killId);
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
    /**
     * 获取秒杀商品
     * @return
     */
    GoodsKillVO getProducts();
}
