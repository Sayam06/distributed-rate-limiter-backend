package com.sayam.rate_limiter_url_shortener_service.repository;

import com.sayam.rate_limiter_url_shortener_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    com.sayam.rate_limiter_url_shortener_service.model.User findByApiKey(String apiKey);
}
