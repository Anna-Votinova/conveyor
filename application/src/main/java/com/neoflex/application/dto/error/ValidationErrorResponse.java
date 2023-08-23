package com.neoflex.application.dto.error;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}