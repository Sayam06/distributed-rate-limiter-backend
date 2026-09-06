package com.sayam.rate_limiter_url_shortener_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String ipAddress;
    String apiKey;
    Instant createdOn;
}
