package com.sayam.rate_limiter_service.controller;

import com.sayam.rate_limiter_service.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("ratelimit")
public class RateController {
    @Autowired
    private RateService rateService;

    @GetMapping("hello")
    public String greet() {
        return "Hello World!";
    }

    @PostMapping("check")
    public boolean checkRateLimit(@RequestHeader("Authorization") String authHeader) {
        return rateService.checkRateLimit(authHeader);
    }
}
