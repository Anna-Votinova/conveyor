package com.neoflex.gateway.dto.error;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}