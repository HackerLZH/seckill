package com.lzh.service;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.utils.SeckillConstants;
import com.rabbitmq.client.Channel;

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
    @RabbitListener(queues = SeckillConstants.MQ_KILL_GOOD_QUEUE)
    public void consumeKillGood(GoodsKillOrder order, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) { 
        try {
            seckikkillService.saveOrder(order);
            if (tryAck(channel, tag)) {
                // 进入订单支付倒计时（延时队列无消费者）
                rabbitTemplate.convertAndSend(
                    SeckillConstants.MQ_KILL_GOOD_ORDER_EXCHANGE
                    , SeckillConstants.MQ_KILL_GOOD_ORDER_ROUTE
                    , order);
            }
        } catch (Exception e) {
            tryNack(channel, tag);
            log.error("订单消费失败：{}", e.getMessage());
        }
        
    }

    /**
     * 订单支付超时处理
     * @param order
     * @throws IOException 
     */
    @RabbitListener(queues = SeckillConstants.MQ_KILL_GOOD_DLX_QUEUE)
    public void consumeKillGoodDLX(GoodsKillOrder order, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            seckikkillService.processTimeOutOrder(order);
            if (!tryAck(channel, tag)) {
                return;
            }
        } catch (Exception e) {
            tryNack(channel, tag);
            log.error("超时订单消费失败：{}", e.getMessage());
        }

    }

    private boolean tryAck(Channel channel, long tag) {
        try {
            channel.basicAck(tag, false);
            return true;
        } catch (IOException e) {
            log.error("消息确认失败：{}", e.getMessage());
            return false;
        }
    }

    private boolean tryNack(Channel channel, long tag) { 
        try {
            channel.basicNack(tag, false, true);
            return true;
        } catch (IOException e) {
            log.error("消息拒绝失败：{}", e.getMessage());        
            return false;
        }   
    }
}
