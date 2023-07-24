package com.neoflex.conveyor.controller.advice;

import java.util.List;
public record ValidationErrorResponse(List<Violation> violations) {

}