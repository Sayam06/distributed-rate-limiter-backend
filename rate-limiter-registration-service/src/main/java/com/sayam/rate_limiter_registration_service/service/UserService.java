package com.sayam.rate_limiter_registration_service.service;

import com.sayam.rate_limiter_registration_service.models.User;
import com.sayam.rate_limiter_registration_service.repository.UserRepository;
import com.sayam.rate_limiter_registration_service.util.ApiKeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<String> createAPIKey(String ipAddress) {
        System.out.println(ipAddress);
        Optional<User> checkIfExistingUser = userRepository.findByIpAddress(ipAddress);
        if(checkIfExistingUser.isPresent()) {
            return new ResponseEntity<>(checkIfExistingUser.get().getApiKey(), HttpStatus.OK);
        }
        ApiKeyGenerator generator = new ApiKeyGenerator(0);
        String apiKey = generator.generateApiKey(ipAddress);
        Instant createdAt = Instant.now();

        User user = new User();
        user.setApiKey(apiKey);
        user.setIpAddress(ipAddress);
        user.setCreatedAt(createdAt);

        userRepository.save(user);
        return new ResponseEntity<>(apiKey, HttpStatus.CREATED);

    }
}
