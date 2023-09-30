package com.neoflex.deal.exception;

import javax.validation.ValidationException;

public class InvalidSesCodeException extends ValidationException {
    public InvalidSesCodeException(String message) {
        super(message);
    }
}
