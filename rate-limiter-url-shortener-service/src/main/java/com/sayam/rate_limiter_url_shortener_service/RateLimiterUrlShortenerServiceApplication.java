package com.sayam.rate_limiter_url_shortener_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RateLimiterUrlShortenerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterUrlShortenerServiceApplication.class, args);
	}

}
