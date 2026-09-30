package com.lzh.utils;

import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatus;

public final class SeckillConstants {
    private SeckillConstants() {}
    // Redis Key
    // 秒杀库存
    public static final String SECKILL_STORE_KEY = "seckill:stock:";
    // 秒杀用户
    public static final String SECKILL_ORDER_KEY = "seckill:order:";
    // 已下单id（集合）
    public static final String SECKILL_ORDER_KILLED = "seckill:order:killed:";

    public static final String CACHE_GOODSID_KILLID = "cache:goodsId:killId:";

    // Rabbitmq key
    public static final String MQ_KILL_GOOD_QUEUE = "kill.good";
    public static final String MQ_KILL_GOOD_EXCHANGE = "kill.good";
    public static final String MQ_KILL_GOOD_ROUTE = "kill.good";
    public static final String MQ_KILL_GOOD_ORDER_QUEUE = "kill.good.order";
    public static final String MQ_KILL_GOOD_ORDER_EXCHANGE = "kill.good.order";
    public static final String MQ_KILL_GOOD_ORDER_ROUTE = "kill.good.order";    
    public static final String MQ_KILL_GOOD_DLX_QUEUE = "kill.good.dlx";
    public static final String MQ_KILL_GOOD_DLX_EXCHANGE = "kill.good.dlx";
    public static final String MQ_KILL_GOOD_DLX_ROUTE = "kill.good.dlx"; 

        // Graceful Code
    public static final class GracefulCode {
        private GracefulCode() {}
        public static final String STOCK_INSUFFICIENT_CODE = "530000";
        public static final String STOCK_INSUFFICIENT_MESSAGE = "库存不足";
        public static final String ORDER_EXIST_CODE = "530001";
        public static final String ORDER_EXIST_MESSAGE = "您已下单";
    }

    public static final DefaultResponseStatus STOCK_INSUFFICIENT = new DefaultResponseStatus(GracefulCode.STOCK_INSUFFICIENT_CODE, GracefulCode.STOCK_INSUFFICIENT_MESSAGE);
    public static final DefaultResponseStatus ORDER_EXIST = new DefaultResponseStatus(GracefulCode.ORDER_EXIST_CODE, GracefulCode.ORDER_EXIST_MESSAGE);
}
