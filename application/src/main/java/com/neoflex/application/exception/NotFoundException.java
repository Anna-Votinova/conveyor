package com.neoflex.application.exception;

import javax.validation.ValidationException;

public class NotFoundException extends ValidationException {
    public NotFoundException(String message) {
        super(message);
    }
}
