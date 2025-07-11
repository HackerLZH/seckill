package com.lzh.utils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置String
     * @param key
     * @param value
     * @param timeout 分钟
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.MINUTES);
    }

    /**
     * 执行Lua脚本
     * @param script
     */
    public Object execute(RedisScript script, List<String> keys, Object... args) {
        return stringRedisTemplate.execute(script, keys, args);
    }

    /**
     * 集合中添加元素
     * @param key
     * @param values
     * @return
     */
    public void sadd(String key, Object value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 集合中添加元素
     * @param timeout 超时（分钟）
     * @param key
     * @param values
     * @return
     */
    public void sadd(String key, Object value, Long timeout) {
        Boolean exists = exists(key);
        redisTemplate.opsForSet().add(key, value);
        Optional.ofNullable(exists).filter(b -> !b).ifPresent(b -> {
            expire(key, timeout);
        });
    }

    /**
     * 移除集合中的元素
     * @param key
     * @param value
     */
    public void spop(String key, Object value) {
        redisTemplate.opsForSet().remove(key, value);
    }
    /**
     * 判断集合成员
     * @param key
     * @param value
     * @return
     */
    public Boolean sismember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    /**
     * 自增
     * @param key
     * @param value
     */
    public void incrby(String key, long value) {
       redisTemplate.opsForValue().increment(key, value);
    }
}
