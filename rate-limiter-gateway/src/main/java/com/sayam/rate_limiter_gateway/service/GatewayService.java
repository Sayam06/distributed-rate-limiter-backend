package com.sayam.rate_limiter_gateway.service;

import com.sayam.rate_limiter_gateway.dto.ShortenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class GatewayService {
    private final RateLimitClient rateLimitClient;
    private final WebClient webClient;
    private final String shortenerUrl;
    private final String registrationUrl;

    public GatewayService(RateLimitClient rateLimitClient,
                             WebClient.Builder webClientBuilder,
                             @Value("${url-shortener.url}") String shortenerUrl, @Value("${registration.url}") String registrationUrl) {
        this.rateLimitClient = rateLimitClient;
        this.webClient = webClientBuilder.build();
        this.shortenerUrl = shortenerUrl;
        this.registrationUrl = registrationUrl;
    }

    public Mono<ResponseEntity<String>> shortenUrl(String authHeader, ShortenRequest requestBody) {
        return rateLimitClient.isAllowed(authHeader)
                .flatMap(allowed -> {

                    if (!allowed) {
                        return Mono.just(
                                ResponseEntity
                                        .status(HttpStatus.TOO_MANY_REQUESTS)
                                        .header("Retry-After", "10")
                                        .body("Rate limit exceeded")
                        );
                    }

                    return webClient.post()
                            .uri(shortenerUrl + "/shorten")
                            .bodyValue(requestBody)
                            .header("Authorization", authHeader)
                            .exchangeToMono(response ->
                                    response.toEntity(String.class)
                            );
                });
    }

    public Mono<ResponseEntity<String>> redirectToActualUrl(String shortUrl) {
        return webClient.get()
                .uri(shortenerUrl + "/" + shortUrl)
                .exchangeToMono(response -> response.toEntity(String.class));
    }

    public Mono<ResponseEntity<String>> createApiKey(ServerWebExchange exchange) {
        String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        System.out.println("Registration URL = " + registrationUrl);
        System.out.println("Calling URL = " + registrationUrl + "/generateAPIKey");
        return webClient.post()
                .uri(registrationUrl + "/generateAPIKey")
                .header("X-Forwarded-For", clientIp)
                .exchangeToMono(response ->
                        response.toEntity(String.class)
                );
    }
}
