环境：

![image](https://github.com/user-attachments/assets/7fb83d6a-a698-4d3c-8737-5af8957a1843)

模块：
- gateway-8080: 网关微服务
- auth-service-8082: 认证微服务
- seckill-service-8081: 秒杀微服务
- common: 公共模块

项目难点：
- 库存超卖，一人一单，原子操作
    - Redis Lua相比Redisson分布式锁的优势？
- 消息队列异步下单，流量削峰
- 熔断操作？
- Redis对于LocalDateTime类型的序列化问题。引入jackson-datatype-jsr310，配置RedisTemplate的序列化方式，设置时间模块。
- 网关聚合API文档
- 部署在frp内网穿透服务器上，对于frps和frpc的配置。
- 开发时本地无法大量启动微服务，爆内存