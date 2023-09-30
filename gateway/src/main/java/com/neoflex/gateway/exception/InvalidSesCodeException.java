package com.neoflex.gateway.exception;

import javax.validation.ValidationException;

public class InvalidSesCodeException extends ValidationException {
    public InvalidSesCodeException(String message) {
        super(message);
    }
}
