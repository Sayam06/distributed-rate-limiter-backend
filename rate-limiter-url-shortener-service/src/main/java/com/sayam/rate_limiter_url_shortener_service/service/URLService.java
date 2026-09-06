package com.sayam.rate_limiter_url_shortener_service.service;

import com.sayam.rate_limiter_url_shortener_service.dto.RequestDTO;
import com.sayam.rate_limiter_url_shortener_service.model.URLMapping;
import com.sayam.rate_limiter_url_shortener_service.model.User;
import com.sayam.rate_limiter_url_shortener_service.repository.RedisRepository;
import com.sayam.rate_limiter_url_shortener_service.repository.URLRepository;
import com.sayam.rate_limiter_url_shortener_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class URLService {
    @Autowired
    private URLRepository urlRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisRepository redisRepository;

    public ResponseEntity<String> shorten(RequestDTO request, String authHeader) {
        String apiKey = authHeader.substring(7);
        String url = request.getUrl();
        String preferredShortenedUrl = request.getPreferredShortenedUrl();


        User requestUser = userRepository.findByApiKey(apiKey);

        if(preferredShortenedUrl.isEmpty()) {
            //user has no preference

            // check if url to be shortened already exists or not
            Optional<URLMapping> existingLongUrl = urlRepository.findByFullUrl(url);

            if(existingLongUrl.isPresent()) {
                return new ResponseEntity<>(existingLongUrl.get().getShortenedUrl(), HttpStatus.OK);
            }

        } else {
            // shortened url already exists
            Optional<URLMapping> existingShortenedUrl = urlRepository.findByShortenedUrl(preferredShortenedUrl);

            // check if already existing shortened url is mapped to the same full url as in request
            if (existingShortenedUrl.isPresent()) {
                if (url.equals(existingShortenedUrl.get().getFullUrl())) {
                    return new ResponseEntity<>(preferredShortenedUrl, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Requested shortened URL is already in use. Please enter a different one",
                            HttpStatus.CONFLICT);
                }
            }
        }

        URLMapping urlMapping = new URLMapping();

        Long id = redisRepository.getURLId();
        urlMapping.setId(id);
        preferredShortenedUrl = preferredShortenedUrl.isEmpty() ? Base64.getEncoder().encodeToString(
                id.toString().getBytes(StandardCharsets.UTF_8)
        ) : preferredShortenedUrl;
        Instant currentTime = Instant.ofEpochMilli(System.currentTimeMillis());


        urlMapping.setId(redisRepository.getURLId());
        urlMapping.setShortenedUrl(preferredShortenedUrl);
        urlMapping.setFullUrl(url);
        urlMapping.setCreatedOn(currentTime);
        urlMapping.setLastRequestOn(currentTime);
        urlMapping.setNumberOfRequests(0);
        urlMapping.setCreatedByUser(requestUser);
        urlRepository.save(urlMapping);

        return new ResponseEntity<>(urlMapping.getShortenedUrl(), HttpStatus.CREATED);



    }

    public ResponseEntity<String> redirectToActualUrl(String shortenedUrl) {
        Optional<URLMapping> existingShortenedUrl = urlRepository.findByShortenedUrl(shortenedUrl);

        if(existingShortenedUrl.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(existingShortenedUrl.get().getFullUrl()))
                    .build();
        }
        return new ResponseEntity<>("Shortened URL is not present", HttpStatus.NOT_FOUND);
    }
}
