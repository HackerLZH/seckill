package com.lzh.utils;

public class Constants {
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
}
