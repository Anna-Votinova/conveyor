package com.neoflex.deal.exception;

import javax.validation.ValidationException;

public class ScoringConveyorException extends ValidationException {
    public ScoringConveyorException(String message) {
        super(message);
    }
}
