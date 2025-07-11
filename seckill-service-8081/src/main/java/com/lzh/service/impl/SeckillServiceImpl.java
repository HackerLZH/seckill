package com.lzh.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lzh.entity.GoodsKillOrder;
import com.lzh.enums.OrderStatus;
import com.lzh.mapper.GoodsKillMapper;
import com.lzh.mapper.GoodsKillOrderMapper;
import com.lzh.response.Result;
import com.lzh.service.ISeckillService;
import com.lzh.utils.SeckillConstants;
import com.lzh.utils.UserHolder;
import com.lzh.utils.RedisUtil;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    public Result kill(Integer killId) {
        Integer userId = UserHolder.getUser().getId();
        // 使用lua脚本实现 扣减库存+一人一单， 保证原子性
        long res = (long) redisUtil.execute(
            SECK_SCRIPT
            , List.of(SeckillConstants.SECKILL_STORE_KEY, SeckillConstants.SECKILL_ORDER_KEY) // 传入keys
            , String.valueOf(userId) // arg1
            , String.valueOf(killId) // arg2
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
                // 无法容忍的时钟回拨，那就重新生成一个id
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
            SeckillConstants.MQ_KILL_GOOD_EXCHANGE
            , SeckillConstants.MQ_KILL_GOOD_ROUTE
            , goodsKillOrder
        );
    
        return Result.success(orderId);
    }

    @Transactional
    @Override
    public void saveOrder(GoodsKillOrder order) {
        try {
            Long orderId = order.getOrderId();
            if (redisUtil.sismember(SeckillConstants.SECKILL_ORDER_KILLED + order.getGoodsKillId(), orderId)) {
                // 消息幂等
                return;
            }
            redisUtil.sadd(SeckillConstants.SECKILL_ORDER_KILLED + order.getGoodsKillId(), orderId, 3L);
            // 查询商品id
            String goodsId = (String)redisUtil.get(SeckillConstants.CACHE_GOODSID_KILLID + order.getGoodsKillId());
            if (Objects.isNull(goodsId)) {
                redisUtil.set(
                    SeckillConstants.CACHE_GOODSID_KILLID + order.getGoodsKillId()
                    , goodsKillMapper.findgoodsId(order.getGoodsKillId())
                    , 3L);
            }
            // 扣减库存
            // goodsMapper.cutStock(goodsId, 1); // 商品总库存待支付后再扣减
            goodsKillMapper.cutStock(order.getGoodsKillId());
            // 保存订单
            goodsKillOrderMapper.insert(order);
        } catch (Exception ex) {
            // redis回滚
            redisUtil.spop(SeckillConstants.SECKILL_ORDER_KILLED + order.getGoodsKillId(), order.getOrderId());
            throw new RuntimeException("下单失败!\t" + ex.getMessage());
        }
    }
    @Transactional
    @Override
    public void processTimeOutOrder(GoodsKillOrder order) {
        // 查询订单状态
        OrderStatus status = goodsKillOrderMapper.selectStatus(order.getOrderId());
        // 如果为WAIT（待付款），则更改为CANCEL（已取消），然后恢复秒杀库存
        Optional.ofNullable(status).filter(s -> s == OrderStatus.WAIT).ifPresent(s -> {
            goodsKillOrderMapper.updateStatus(order.getOrderId(), OrderStatus.CANCEL);
            goodsKillMapper.addStock(order.getGoodsKillId(), 1);
        });
    }
}
