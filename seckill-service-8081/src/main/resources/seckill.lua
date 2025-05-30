local userId = ARGV[1]
local killId = ARGV[2]

-- 库存key
local stockKey = 'seckill:stock:' .. killId
-- 订单key
local orderKey = 'seckill:order:' .. killId

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
return 0