-- 订单消费幂等性保证

local key = KEYS[1]
local value = ARGV[1]
local ttl = ARGV[2]
if redis.call('SADD', key, value) == 1 then
    return redis.call('EXPIRE', key, ttl)
else
    -- 集合中有订单号已存在，添加失败
    return 0
end