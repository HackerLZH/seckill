package com.lzh.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 * <p>
 * 缓存策略：
 * - products: 秒杀商品列表，TTL=10s（库存变化频繁，需及时刷新）
 * </p>
 * <p>
 * 刷新方式：
 * 1. TTL 自动过期
 * 2. 写操作 @CacheEvict 主动清除（同 JVM 内调用）
 * 3. /internal/cache/evict 接口强制清除（跨服务调用，如 admin-service 修改商品后通知）
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /** 秒杀商品列表缓存名 */
    public static final String CACHE_PRODUCTS = "products";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(10)
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.SECONDS) // ttl=10s
                .recordStats()); // 监控命中率
        // 只缓存显式声明的 cache name
        cacheManager.setCacheNames(List.of(CACHE_PRODUCTS));
        return cacheManager;
    }
}
