local userId = ARGV[1]
local killId = ARGV[2]
local ttl = ARGV[3] -- ms

-- 库存key
local stockKey = KEYS[1] .. killId
-- 订单key
local orderKey = KEYS[2] .. killId

local exists = redis.call('EXISTS', orderKey)

if (tonumber(redis.call('get', stockKey)) <= 0) then
    -- 库存不足
    return 1
end

if (tonumber(redis.call('sismember', orderKey, userId)) == 1) then
    -- 重复秒杀
    return 2
end

-- 扣减库存
redis.call('incrby', stockKey, -1)
-- 添加订单
redis.call('sadd', orderKey, userId)

if (exists == 0) then
    redis.call('PEXPIRE', orderKey, ttl)
end

return 0