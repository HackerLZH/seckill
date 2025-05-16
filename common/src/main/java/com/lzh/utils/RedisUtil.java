package com.lzh.utils;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 执行Lua脚本
     * @param script
     */
    public Object execute(RedisScript script, Object... args) {
        return stringRedisTemplate.execute(script, Collections.emptyList(), args);
    }

    /**
     * 移除集合中的元素
     * @param key
     * @param value
     */
    public void spop(String key, Object value) {
        stringRedisTemplate.opsForSet().remove(key, value);
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
