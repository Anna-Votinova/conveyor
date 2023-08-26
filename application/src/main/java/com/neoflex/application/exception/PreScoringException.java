package com.neoflex.application.exception;

import com.neoflex.application.dto.error.Violation;

import javax.validation.ValidationException;
import java.util.List;

public class PreScoringException extends ValidationException {

    private final List<Violation> violations;

    public PreScoringException(String message, List<Violation> violations) {
        super(message);
        this.violations = violations;
    }

    public List<Violation> getViolations() {
        return violations;
    }
}
