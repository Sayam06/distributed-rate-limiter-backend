package com.sayam.rate_limiter_registration_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RateLimiterRegistrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterRegistrationServiceApplication.class, args);
	}

}
