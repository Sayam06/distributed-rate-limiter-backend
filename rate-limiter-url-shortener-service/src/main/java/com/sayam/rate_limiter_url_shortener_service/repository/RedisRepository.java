package com.sayam.rate_limiter_url_shortener_service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Long getURLId() {
        return redisTemplate.opsForValue().increment("url:id");
    }
}
