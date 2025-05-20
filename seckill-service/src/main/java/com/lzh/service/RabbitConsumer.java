package com.lzh.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RabbitConsumer {
    @Autowired
    private ISeckillService seckikkillService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 处理秒杀订单
     * @param order
     * //TODO: 监听器写的太简单了？
     */
    @RabbitListener(queues = Constants.MQ_KILL_GOOD_QUEUE)
    public void consumeKillGood(GoodsKillOrder order) { 
        try {
            seckikkillService.saveOrder(order);
        } catch (RuntimeException re) {
            // 下单失败了
            log.info(re.getMessage());
            return;
        }
        // 进入订单支付倒计时（延时队列无消费者）
        rabbitTemplate.convertAndSend(
            Constants.MQ_KILL_GOOD_ORDER_EXCHANGE
            , Constants.MQ_KILL_GOOD_ORDER_ROUTE
            , order);
    }

    /**
     * 订单支付超时处理
     * @param order
     */
    @RabbitListener(queues = Constants.MQ_KILL_GOOD_DLX_QUEUE)
    public void consumeKillGoodDLX(GoodsKillOrder order) {
        seckikkillService.processTimeOutOrder(order);
    }
}
