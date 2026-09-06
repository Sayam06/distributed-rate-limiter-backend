package com.sayam.rate_limiter_gateway.controller;

import com.sayam.rate_limiter_gateway.dto.ShortenRequest;
import com.sayam.rate_limiter_gateway.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@RestController
@CrossOrigin("http://localhost:5173")
public class GatewayController {
    @Autowired
    private GatewayService gatewayService;

    @PostMapping("register")
    public Mono<ResponseEntity<String>> createApiKey(ServerWebExchange exchange) {
        return gatewayService.createApiKey(exchange);
    }


    @PostMapping("/shortenURL")
    public Mono<ResponseEntity<String>> shortenUrl(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ShortenRequest requestBody) {
        return gatewayService.shortenUrl(authHeader, requestBody);
    }

    @GetMapping("{shortUrl}")
    public Mono<ResponseEntity<String>> redirectToActualUrl(@PathVariable String shortUrl) {
        return gatewayService.redirectToActualUrl(shortUrl);
    }
}
