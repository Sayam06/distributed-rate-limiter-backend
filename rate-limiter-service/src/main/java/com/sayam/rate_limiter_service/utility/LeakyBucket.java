package com.sayam.rate_limiter_service.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;

@Component
public class LeakyBucket {
    private final double capacity = 5;  // bucket size
    private final double leakRate = 0.1;  // tokens leaked per second

    HashMap<String, Double> keyWater = new HashMap<>();
    HashMap<String, Long> keyTimestamp = new HashMap<>();

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedisScript<Long> leakyBucketScript;


    public boolean allow(String apiKey) {
        String key = "ratelimit:" + apiKey;
        long now = System.currentTimeMillis();

        Long result = redisTemplate.execute(
                leakyBucketScript,
                Collections.singletonList(key),                  // KEYS[1]
                String.valueOf(capacity),                          // ARGV[1]
                String.valueOf(leakRate),                          // ARGV[2]
                String.valueOf(now)                                // ARGV[3]
        );

        return result != null && result == 1L;
    }

    private double leak(String apiKey) {
        long now = System.currentTimeMillis();

        double water = keyWater.containsKey(apiKey) ? keyWater.get(apiKey) : 0;
        double lastLeakTimestamp = keyTimestamp.containsKey(apiKey) ? keyTimestamp.get(apiKey) : now;

        double elapsedSeconds = (now - lastLeakTimestamp) / 1000.0;
        water = Math.max(0, water - elapsedSeconds * leakRate);


        keyWater.put(apiKey, water);
        keyTimestamp.put(apiKey, now);

        return water;
    }
}
