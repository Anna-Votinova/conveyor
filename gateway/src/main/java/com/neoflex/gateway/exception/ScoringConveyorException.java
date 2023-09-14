package com.neoflex.gateway.exception;

import javax.validation.ValidationException;

public class ScoringConveyorException extends ValidationException {
    public ScoringConveyorException(String message) {
        super(message);
    }
}
