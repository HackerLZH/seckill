package com.lzh.service;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.entity.GoodsKillOrderVO;
import com.lzh.entity.GoodsKillVO;
import com.lzh.enums.OrderStatus;

import java.util.List;

public interface ISeckillService {
    /**
     * 秒杀
     * @param killId
     * @return 订单id (JS精度不如Java，因此String类型返回)
     */
    String kill(Integer killId);
    /**
     * 下订单
     * @param order
     */
    void saveOrder(GoodsKillOrder order);
    /**
     * 处理订单状态
     * @param order
     * @param status
     */
    void updateOrderStatus(GoodsKillOrder order, OrderStatus status);
    /**
     * 获取秒杀商品
     * @return
     */
    GoodsKillVO getProducts();

    /**
     * 获取秒杀订单
     * @return
     */
    List<GoodsKillOrderVO> getKillOrders();

    boolean checkOrder(Long orderId, Integer userId, OrderStatus orderStatus);

    /**
     * 支付成功后处理
     * @param orderId
     */
    void postProcess(Long orderId);
}
