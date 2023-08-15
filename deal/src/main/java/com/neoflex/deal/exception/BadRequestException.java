package com.neoflex.deal.exception;

import javax.validation.ValidationException;

public class BadRequestException extends ValidationException {
    public BadRequestException(String message) {
        super(message);
    }
}
