package com.neoflex.conveyor.dto.error;

import java.util.List;
public record ValidationErrorResponse(List<Violation> violations) {
}