package com.lzh.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.response.Result;
import com.lzh.service.ISeckillService;
import com.lzh.utils.Constants;
import com.lzh.utils.RedisUtil;

import cn.hutool.core.lang.Snowflake;

@Service
public class SeckillServiceImpl implements ISeckillService {
    @Autowired
    private Snowflake snowflake;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static DefaultRedisScript <Long> SECK_SCRIPT = new DefaultRedisScript<>();
    static {
        SECK_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public Result kill(Integer killId, Integer userId) {
        // TODO: 通过ThreadLocal获取用户id
        // 使用lua脚本实现 扣减库存+一人一单， 保证原子性
        long res = (long) redisUtil.execute(
            SECK_SCRIPT
            , String.valueOf(userId)
            , String.valueOf(killId)
        );

        if (res == 1) {
            return Result.fail("库存不足");
        }
        if (res == 2) {
            return Result.fail("一人一单");
        }
        long orderId = 0L;
        while (orderId == 0L) {
            try {
                orderId = snowflake.nextId();
            } catch (IllegalStateException ise) {
                //出现时钟回拨报错，那就重新生成一个id
            }
        }
        // 创建订单对象
        GoodsKillOrder goodsKillOrder = GoodsKillOrder.builder()
                .orderId(orderId)
                .userId(userId)
                .goodsKillId(killId)
                .build();
        // MQ异步处理订单
        rabbitTemplate.convertAndSend(
            Constants.MQ_KILL_GOOD_EXCHANGE
            , Constants.MQ_KILL_GOOD_ROUTE
            , goodsKillOrder
        );
    
        return Result.success(orderId);
    }

}
