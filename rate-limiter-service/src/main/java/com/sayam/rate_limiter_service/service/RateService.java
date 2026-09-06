package com.sayam.rate_limiter_service.service;

import com.sayam.rate_limiter_service.repository.RateRepository;
import com.sayam.rate_limiter_service.utility.LeakyBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RateService {
    @Autowired
    private RateRepository repository;
    @Autowired
    private LeakyBucket leakyBucket;

    public boolean checkRateLimit(String authHeader) {
        String apiKey = authHeader.substring(7);

        return leakyBucket.allow(apiKey);
    }
}
