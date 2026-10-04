package com.lzh.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.alibaba.cloud.nacos.annotation.NacosConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feiniaojin.gracefulresponse.GracefulResponse;
import com.lzh.entity.GoodsKillOrder;
import com.lzh.entity.GoodsKillOrderVO;
import com.lzh.entity.GoodsKillVO;
import com.lzh.enums.OrderStatus;
import com.lzh.mapper.GoodsKillMapper;
import com.lzh.mapper.GoodsKillOrderMapper;
import com.lzh.mapper.GoodsMapper;
import com.lzh.service.ISeckillService;
import com.lzh.utils.RedisUtil;
import com.lzh.utils.SeckillConstants;
import com.lzh.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private GoodsMapper goodsMapper;
    @Autowired
    private ObjectMapper objectMapper;

    @NacosConfig(dataId = "seckill-service.yml", group = "DEFAULT_GROUP", key = "order.timeout")
    Long orderTimeout;

    private static DefaultRedisScript<Long> SECK_SCRIPT = new DefaultRedisScript<>();
    private static DefaultRedisScript<Long> ORDERIDEMP_SCRIPT = new DefaultRedisScript<>();

    static {
        SECK_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECK_SCRIPT.setResultType(Long.class);
        ORDERIDEMP_SCRIPT.setLocation(new ClassPathResource("orderidemp.lua"));
        ORDERIDEMP_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void kill(Integer killId) {
        try {
            Integer userId = UserHolder.getUser().getId();
            // 使用lua脚本实现 扣减库存+一人一单， 保证原子性
            long res = (long) redisUtil.execute(
                    SECK_SCRIPT
                    , List.of(SeckillConstants.SECKILL_STORE_KEY, SeckillConstants.SECKILL_ORDER_KEY) // 传入keys
                    , String.valueOf(userId) // arg1
                    , String.valueOf(killId) // arg2
                    , orderTimeout.toString() // arg3
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
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setExpiration("60000"); // 60s
            Message message = null;
            try {
                message = new Message(objectMapper.writeValueAsBytes(goodsKillOrder), new MessageProperties());
            } catch (JsonProcessingException e) {
                GracefulResponse.raiseException("500", "订单转byte[]异常");
            }

            // MQ异步处理订单
            rabbitTemplate.convertAndSend(
                    SeckillConstants.MQ_KILL_GOOD_EXCHANGE
                    , SeckillConstants.MQ_KILL_GOOD_ROUTE
                    , message
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Transactional
    @Override
    public void saveOrder(GoodsKillOrder order) {
        try {
            String killedId = SeckillConstants.SECKILL_ORDER_KILLED + order.getGoodsKillId();
            // 消息幂等（Redis Set）
            Long res = (Long) redisUtil.execute(
                    ORDERIDEMP_SCRIPT
                    , List.of(killedId) // 缓存key
                    , order.getOrderId().toString() // 缓存value
                    , orderTimeout.toString() // 过期时间
            );
            if (res == 0) {
                log.warn("消息重复处理，skip：orderId={}", order.getOrderId());
                // throw new RuntimeException("消息重复处理");
                return;
            }

            // 保存订单
            goodsKillOrderMapper.insert(order);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderStatus(GoodsKillOrder order, OrderStatus status) {
        try {
            // 查询订单状态
            OrderStatus cur = goodsKillOrderMapper.selectStatus(order.getOrderId());
            // 如果为WAIT（待付款），则更改为指定状态
            Optional.ofNullable(cur).filter(s -> s == OrderStatus.WAIT).ifPresent(s -> {
                goodsKillOrderMapper.updateStatus(order.getOrderId(), status);
            });
            log.info("订单{}，状态更新为{}", order.getOrderId(), status.getValue());
        } catch (Exception e) {
            log.error("订单{}, 状态更新失败", order.getOrderId(), e);
        }
    }

    @Override
    public GoodsKillVO getProducts() {
        try {
            List<GoodsKillVO.GoodsKillInfo> currentList = goodsKillMapper.selectCurrent();
            List<GoodsKillVO.GoodsKillInfo> upcoming = goodsKillMapper.selectUpcoming();

            // 从redis获取availableStock，写入currentList
            List<GoodsKillVO.GoodsKillInfo> current = currentList.stream().map(item -> {
                GoodsKillVO.GoodsKillInfo info = new GoodsKillVO.GoodsKillInfo();
                info.setName(item.getName());
                info.setImage(item.getImage());
                info.setPrice(item.getPrice());
                info.setStartTime(item.getStartTime());
                info.setStock(item.getStock());

                Integer availableStock = (Integer) redisUtil.get(SeckillConstants.SECKILL_STORE_KEY + item.getId());
                info.setAvailableStock(availableStock != null ? availableStock : 0);

                return info;
            }).collect(Collectors.toList());

            return GoodsKillVO.builder().current(current).upcoming(upcoming).build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<GoodsKillOrderVO> getKillOrders() {
        try {
            return goodsKillOrderMapper.findOrdersByUserId(UserHolder.getUser().getId());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean checkOrder(Long orderId, Integer userId, OrderStatus orderStatus) {
        try {
            GoodsKillOrder goodsKillOrder = goodsKillOrderMapper.findKillOrderByOrderId(orderId);
            return !Objects.isNull(goodsKillOrder)
                    && userId.equals(goodsKillOrder.getUserId())
                    && orderStatus == goodsKillOrder.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    @Override
    public void postProcess(Long orderId) {
        try {
            // 状态更新
            updateOrderStatus(GoodsKillOrder.builder().orderId(orderId).build(), OrderStatus.PAID);
            // 扣减库存
            GoodsKillOrder order = goodsKillOrderMapper.findKillOrderByOrderId(orderId);
            if (order == null) {
                throw new NoSuchElementException(String.format("订单%d不存在", orderId));
            }

            Integer killId = order.getGoodsKillId();
            int killRows = goodsKillMapper.cutStock(killId);
            if (killRows == 0) {
                throw new RuntimeException(String.format("秒杀%d库存不足", killId));
            }

            Integer goodsId = goodsKillMapper.findGoodsId(killId);
            if (goodsId == null) {
                throw new NoSuchElementException(String.format("商品%d不存在", goodsId));
            }

            int goodsRows = goodsMapper.cutStock(goodsId, 1);
            if (goodsRows == 0) {
                throw new RuntimeException(String.format("商品%d库存不足", goodsId));
            }
        } catch (Exception e) {
            // 系统异常：兜底，记录详细日志
            log.error("支付后处理异常，orderId={}", orderId, e);
            throw e;
        }
    }

}
