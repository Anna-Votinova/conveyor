package com.neoflex.deal.exception;

import javax.validation.ValidationException;

public class BadRequestConveyorException extends ValidationException {
    public BadRequestConveyorException(String message) {
        super(message);
    }
}
