package com.sayam.rate_limiter_url_shortener_service.controller;

import com.sayam.rate_limiter_url_shortener_service.dto.RequestDTO;
import com.sayam.rate_limiter_url_shortener_service.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("urlShortener")
public class URLController {
    @Autowired
    private URLService urlService;

    @GetMapping("hello")
    public String greet() {
        return "Hello World!";
    }

    @PostMapping("shorten")
    public ResponseEntity<String> post(@RequestBody RequestDTO request, @RequestHeader("Authorization") String authHeader) {
        return urlService.shorten(request, authHeader);
    }

    @GetMapping("{shortenedUrl}")
    public ResponseEntity<String> redirectToActualUrl(@PathVariable String shortenedUrl) {
        return urlService.redirectToActualUrl(shortenedUrl);
    }
}
