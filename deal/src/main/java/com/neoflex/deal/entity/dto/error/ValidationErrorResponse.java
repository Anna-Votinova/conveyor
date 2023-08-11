package com.neoflex.deal.entity.dto.error;

import java.util.List;
public record ValidationErrorResponse(List<Violation> violations) {

}