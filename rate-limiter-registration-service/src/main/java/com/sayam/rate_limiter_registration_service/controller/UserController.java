package com.sayam.rate_limiter_registration_service.controller;

import com.sayam.rate_limiter_registration_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("register")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("hello")
    public String greet() {
        return "Hello World!";
    }

    @PostMapping("generateAPIKey")
    public ResponseEntity<String> createAPIKey(@RequestHeader("X-Forwarded-For") String clientIp) {
        try {
            return userService.createAPIKey(clientIp);
        } catch (Exception ex) {
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
