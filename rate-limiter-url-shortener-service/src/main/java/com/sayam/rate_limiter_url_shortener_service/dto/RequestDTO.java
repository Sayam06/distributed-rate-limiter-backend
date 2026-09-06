package com.sayam.rate_limiter_url_shortener_service.dto;

import lombok.Data;

@Data
public class RequestDTO {
    private String url;
    private String preferredShortenedUrl;
}
