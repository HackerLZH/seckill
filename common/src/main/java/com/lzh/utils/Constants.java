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

    // YML Key
    public static final String MQ_KILL_GOOD_QUEUE = "kill.good";
    public static final String MQ_KILL_GOOD_EXCHANGE = "kill.good";
    public static final String MQ_KILL_GOOD_ROUTE = "kill.good";
}
