-- 订单消费幂等性保证

local key = KEYS[1]
local value = ARGV[1]
local ttl = ARGV[2]

local exists = redis.call('EXISTS', key)
local added = redis.call('SADD', key, value)

-- 只在 Set 创建时设置一次 TTL
if (exists == 0) then
    redis.call('PEXPIRE', key, ttl)
end

-- 返回0表示订单已存在
return added