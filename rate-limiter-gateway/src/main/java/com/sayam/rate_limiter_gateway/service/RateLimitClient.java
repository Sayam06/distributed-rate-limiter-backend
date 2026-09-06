package com.sayam.rate_limiter_gateway.service;

import com.sayam.rate_limiter_gateway.exceptions.InvalidApiKeyException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;


@Service
public class RateLimitClient {

    private final WebClient webClient;
    private final String rateLimiterUrl;

    public RateLimitClient(WebClient.Builder webClientBuilder,
                           @Value("${rate-limiter.url}") String rateLimiterUrl) {
        this.webClient = webClientBuilder.build();
        this.rateLimiterUrl = rateLimiterUrl;
    }

    public Mono<Boolean> isAllowed(String authHeader) {
        return webClient.post()
                .uri(rateLimiterUrl)
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                        return Mono.error(new InvalidApiKeyException("Invalid API key"));
                    }
                    return response.createException();
                })
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientRequestException.class, e -> Mono.just(true)); // fail-open on connect/timeout errors
    }
}
