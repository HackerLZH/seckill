package com.lzh.utils;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import cn.hutool.json.JSONUtil;


@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置String
     * @param key
     * @param value
     * @param timeout 分钟
     */
    public void set(String key, Object value, long timeout) {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(key, (String)value, timeout, TimeUnit.MINUTES);            
        } else {
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), timeout, TimeUnit.MINUTES);
        }
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 执行Lua脚本
     * @param script
     */
    public Object execute(RedisScript script, Object... args) {
        return stringRedisTemplate.execute(script, Collections.emptyList(), args);
    }

    /**
     * 集合中添加元素
     * @param key
     * @param values
     * @return
     */
    public void sadd(String key, Object value) {
        if (value instanceof String) {
            stringRedisTemplate.opsForSet().add(key, (String)value);   
        } else {
            stringRedisTemplate.opsForSet().add(key, JSONUtil.toJsonStr(value));
        }
    }

    /**
     * 集合中添加元素
     * @param timeout 超时（分钟）
     * @param key
     * @param values
     * @return
     */
    public void sadd(Long timeout, String key, Object value) {
        sadd(key, value);
        stringRedisTemplate.expire(key, timeout, TimeUnit.MINUTES);
    }

    /**
     * 移除集合中的元素
     * @param key
     * @param value
     */
    public void spop(String key, Object value) {
        if (value instanceof String) {
            stringRedisTemplate.opsForSet().remove(key, (String)value);
        } else {
            stringRedisTemplate.opsForSet().remove(key, JSONUtil.toJsonStr(value));
        }     
    }
    /**
     * 判断集合成员
     * @param key
     * @param value
     * @return
     */
    public Boolean sismember(String key, Object value) {
        if (value instanceof String) {
            return stringRedisTemplate.opsForSet().isMember(key, (String)value);
        } else
        return stringRedisTemplate.opsForSet().isMember(key, JSONUtil.toJsonStr(value));
    }
    /**
     * 自增
     * @param key
     * @param value
     */
    public void incrby(String key, long value) {
        stringRedisTemplate.opsForValue().increment(key, value);
    }
}
