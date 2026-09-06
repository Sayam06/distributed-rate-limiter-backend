package com.sayam.rate_limiter_gateway.exceptions;

public class InvalidApiKeyException extends RuntimeException {
    public InvalidApiKeyException(String message) { super(message); }
}

