package com.sayam.rate_limiter_gateway.dto;

public record ShortenRequest(String url, String preferredShortenedUrl) {}
