package com.lzh.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.GracefulResponse;
import com.lzh.entity.GoodsKillOrder;
import com.lzh.entity.GoodsKillVO;
import com.lzh.enums.OrderStatus;
import com.lzh.mapper.GoodsKillMapper;
import com.lzh.mapper.GoodsKillOrderMapper;
import com.lzh.service.ISeckillService;
import com.lzh.utils.RedisUtil;
import com.lzh.utils.SeckillConstants;
import com.lzh.utils.UserHolder;

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
    @Autowired
    private ObjectMapper objectMapper;
    // @Autowired
    // private Redisson redisson;

    private static DefaultRedisScript <Long> SECK_SCRIPT = new DefaultRedisScript<>();
    private static DefaultRedisScript <Long> ORDERIDEMP_SCRIPT = new DefaultRedisScript<>();
    static {
        SECK_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECK_SCRIPT.setResultType(Long.class);
        ORDERIDEMP_SCRIPT.setLocation(new ClassPathResource("orderidemp.lua"));
        ORDERIDEMP_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void kill(Integer killId) {
        Integer userId = UserHolder.getUser().getId();
        // 使用lua脚本实现 扣减库存+一人一单， 保证原子性
        long res = (long) redisUtil.execute(
            SECK_SCRIPT
            , List.of(SeckillConstants.SECKILL_STORE_KEY, SeckillConstants.SECKILL_ORDER_KEY) // 传入keys
            , String.valueOf(userId) // arg1
            , String.valueOf(killId) // arg2
        );

        if (res == 1) {
            GracefulResponse.raiseException(SeckillConstants.STOCK_INSUFFICIENT);
        }
        if (res == 2) {
            GracefulResponse.raiseException(SeckillConstants.ORDER_EXIST);
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
        // 设置消息TTL
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setExpiration("60000"); // 60s
        Message message = null;
        try {
            message = new Message(objectMapper.writeValueAsBytes(goodsKillOrder), messageProperties);
        } catch (JsonProcessingException e) {
            GracefulResponse.raiseException("500", "订单转byte[]异常");
        }

        // MQ异步处理订单
        rabbitTemplate.convertAndSend(
            SeckillConstants.MQ_KILL_GOOD_EXCHANGE
            , SeckillConstants.MQ_KILL_GOOD_ROUTE
            , message
        );
    }

    // @Transactional
    @Override
    public void saveOrder(GoodsKillOrder order) {
        String killedId = SeckillConstants.SECKILL_ORDER_KILLED + order.getGoodsKillId();
        // 消息幂等（Redis Set）
        Long res = (Long) redisUtil.execute(
            ORDERIDEMP_SCRIPT
            , List.of(killedId) // 缓存key
            , order.getOrderId().toString() // 缓存value
            , "1800" // 过期时间
        );
        if (res == 0) {
            log.info("消息重复处理，skip：orderId={}", order.getOrderId());
            // throw new RuntimeException("消息重复处理");
            return;
        }

        // 查询商品id （分布式锁）
        // String cacheKey = SeckillConstants.CACHE_GOODSID_KILLID + order.getGoodsKillId();
        // RLock rlock = redisson.getLock(cacheKey);
        // rlock.lock();
        // try {
        //     String goodsId = (String)redisUtil.get(cacheKey);
        //     if (Objects.isNull(goodsId)) {
        //         redisUtil.set(
        //             cacheKey
        //             , goodsKillMapper.findgoodsId(order.getGoodsKillId())
        //             , 3L);                        
        //     }
        // } finally {
        //     rlock.unlock();
        // }
        // 扣减库存
        // goodsMapper.cutStock(goodsId, 1); // 商品总库存待支付后再扣减

        // 锁竞争激烈导致slow sql，等支付后统一扣减
        // goodsKillMapper.cutStock(order.getGoodsKillId());

        // 保存订单
        goodsKillOrderMapper.insert(order);
    }
    // @Transactional
    @Override
    public void processTimeOutOrder(GoodsKillOrder order) {
        // 查询订单状态
        OrderStatus status = goodsKillOrderMapper.selectStatus(order.getOrderId());
        // 如果为WAIT（待付款），则更改为CANCEL（已取消）
        Optional.ofNullable(status).filter(s -> s == OrderStatus.WAIT).ifPresent(s -> {
            goodsKillOrderMapper.updateStatus(order.getOrderId(), OrderStatus.CANCEL);
            // goodsKillMapper.addStock(order.getGoodsKillId(), 1);
        });
    }

    @Override
    public GoodsKillVO getProducts() {
        List<GoodsKillVO.GoodsKillInfo> currentList = goodsKillMapper.selectCurrent();
        List<GoodsKillVO.GoodsKillInfo> upcoming = goodsKillMapper.selectUpcoming();

        // 从redis获取availableStock，写入currentList
        List<GoodsKillVO.GoodsKillInfo> current = currentList.stream().map(item -> {
            GoodsKillVO.GoodsKillInfo info = new GoodsKillVO.GoodsKillInfo();
            info.setName(item.getName());
            info.setPrice(item.getPrice());
            info.setStartTime(item.getStartTime());
            info.setStock(item.getStock());
            
            Integer availableStock = (Integer)redisUtil.get(SeckillConstants.SECKILL_STORE_KEY + item.getId());
            info.setAvailableStock(availableStock);

            return info;
        }).collect(Collectors.toList());

        return GoodsKillVO.builder().current(current).upcoming(upcoming).build();
    }
}
