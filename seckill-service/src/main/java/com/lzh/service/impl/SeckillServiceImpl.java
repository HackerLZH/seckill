package com.lzh.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.mapper.GoodsKillMapper;
import com.lzh.mapper.GoodsKillOrderMapper;
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
    @Autowired
    private GoodsKillOrderMapper goodsKillOrderMapper;
    @Autowired
    private GoodsKillMapper goodsKillMapper;

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
                orderId = 0L;
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

    @Transactional
    @Override
    public void saveOrder(GoodsKillOrder order) {
        try {
            String orderId = String.valueOf(order.getOrderId());
            if (redisUtil.sismember(Constants.SECKILL_ORDER_KILLED + order.getGoodsKillId(), orderId)) {
                // 消息幂等
                return;
            }
            redisUtil.sadd(Constants.SECKILL_ORDER_KILLED + order.getGoodsKillId(), orderId, 3L);
            // 查询商品id
            String goodsId = redisUtil.get(Constants.CACHE_GOODSID_KILLID + order.getGoodsKillId());
            if (Objects.isNull(goodsId)) {
                redisUtil.set(
                    Constants.CACHE_GOODSID_KILLID + order.getGoodsKillId()
                    , String.valueOf(goodsKillMapper.findgoodsId(order.getGoodsKillId()))
                    , 3L);
            }
            // 扣减库存
            // goodsMapper.cutStock(goodsId, 1); // 商品总库存待支付后再扣减
            goodsKillMapper.cutStock(order.getGoodsKillId());
            // 保存订单
            goodsKillOrderMapper.insert(order);
        } catch (Exception ex) {
            // redis回滚
            redisUtil.spop(Constants.SECKILL_ORDER_KILLED + order.getGoodsKillId(), order.getOrderId());
            throw new RuntimeException("下单失败!\t" + ex.getMessage());
        }
    }
    @Transactional
    @Override
    public void processTimeOutOrder(GoodsKillOrder order) {
        // 查询订单状态
        Integer status = goodsKillOrderMapper.selectStatus(order.getOrderId());
        // 如果为0（待付款），则更改为2（已取消），然后恢复秒杀库存
        Optional.ofNullable(status).filter(s -> s == 0).ifPresent(s -> {
            goodsKillOrderMapper.updateStatus(order.getOrderId(), 2);
            goodsKillMapper.addStock(order.getGoodsKillId(), 1);
        });
    }
}
