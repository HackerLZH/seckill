package com.lzh.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    /**
     * 处理秒杀订单
     * @param order
     * //TODO: 监听器写的太简单了？
     */
    @RabbitListener(queues = Constants.MQ_KILL_GOOD_QUEUE)
    public void consumeKillGood(GoodsKillOrder order) { 
        seckikkillService.saveOrder(order);
    }
}
