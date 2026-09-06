package com.sayam.rate_limiter_service_registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RateLimiterServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterServiceRegistryApplication.class, args);
	}

}
