package com.neoflex.deal.dto.error;

import java.util.List;

public record ValidationErrorResponse(List<Violation> violations) {
}