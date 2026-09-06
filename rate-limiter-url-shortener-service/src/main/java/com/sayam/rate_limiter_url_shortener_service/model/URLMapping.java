package com.sayam.rate_limiter_url_shortener_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
@Entity
@Table(name = "Url")
public class URLMapping {
    @Id
    Long id;
    String fullUrl;
    String shortenedUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    User createdByUser;
    Integer numberOfRequests;
    Instant createdOn;
    Instant lastRequestOn;
}
