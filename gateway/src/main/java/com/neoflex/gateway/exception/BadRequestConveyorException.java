package com.neoflex.gateway.exception;

import javax.validation.ValidationException;

public class BadRequestConveyorException extends ValidationException {
    public BadRequestConveyorException(String message) {
        super(message);
    }
}
