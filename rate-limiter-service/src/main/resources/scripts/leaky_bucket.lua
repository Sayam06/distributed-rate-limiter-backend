-- KEYS[1] = redis key for this apiKey's bucket
-- ARGV[1] = capacity
-- ARGV[2] = leakRate (tokens per second)
-- ARGV[3] = now (epoch millis)

local water = tonumber(redis.call("HGET", KEYS[1], "water") or "0")
local lastTs = tonumber(redis.call("HGET", KEYS[1], "ts") or ARGV[3])
local capacity = tonumber(ARGV[1])
local leakRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local elapsedSeconds = (now - lastTs) / 1000.0
water = math.max(0, water - elapsedSeconds * leakRate)

local allowed = 0
if water < capacity then
    water = water + 1
    allowed = 1
end

redis.call("HSET", KEYS[1], "water", tostring(water), "ts", tostring(now))
redis.call("EXPIRE", KEYS[1], 3600)

return allowed