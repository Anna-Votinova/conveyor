package com.neoflex.deal.controller.advice;

import com.neoflex.deal.entity.dto.error.ErrorResponse;
import com.neoflex.deal.entity.dto.error.ValidationErrorResponse;
import com.neoflex.deal.entity.dto.error.Violation;
import com.neoflex.deal.exception.JsonParseSqlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Непредвиденная ошибка: ", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Ошибка введенных данных: ", e.getMessage());
    }

    @ExceptionHandler(JsonParseSqlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notProperClientCategoryException(JsonParseSqlException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Введенные данные не соответствуют требованиям: ", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                                      .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                                      .toList();
        return new ValidationErrorResponse(violations);
    }
}