package com.neoflex.gateway.exception;

public class UnknownClientException extends RuntimeException {
    public UnknownClientException(String message) {
        super(message);
    }
}
