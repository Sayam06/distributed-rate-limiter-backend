package com.sayam.rate_limiter_url_shortener_service.repository;

import com.sayam.rate_limiter_url_shortener_service.model.URLMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface URLRepository extends JpaRepository<URLMapping, Integer> {
    Optional<URLMapping> findByFullUrl(String url);

    Optional<URLMapping> findByShortenedUrl(String preferredShortenedUrl);
}
