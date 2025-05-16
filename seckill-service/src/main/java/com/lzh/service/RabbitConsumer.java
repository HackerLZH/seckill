package com.lzh.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.utils.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RabbitConsumer {

    @RabbitListener(queues = Constants.MQ_KILL_GOOD_QUEUE)
    public void consumeKillGood(GoodsKillOrder order) {
        log.info("receive order: {}", order);
    }
}
