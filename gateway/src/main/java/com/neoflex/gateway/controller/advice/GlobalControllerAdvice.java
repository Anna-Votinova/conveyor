package com.neoflex.gateway.controller.advice;

import com.neoflex.gateway.dto.error.ErrorResponse;
import com.neoflex.gateway.dto.error.ValidationErrorResponse;
import com.neoflex.gateway.dto.error.Violation;
import com.neoflex.gateway.exception.ApplicationNotFoundException;
import com.neoflex.gateway.exception.BadRequestException;
import com.neoflex.gateway.exception.InvalidSesCodeException;
import com.neoflex.gateway.exception.UnknownClientException;
import com.neoflex.gateway.exception.UnknownServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Непредвиденная ошибка на стороне сервера: ", e.getMessage());
    }

    @ExceptionHandler(UnknownServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknownServerException(UnknownServerException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Непредвиденная ошибка: ", e.getMessage());
    }

    @ExceptionHandler(UnknownClientException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUnknownClientException(UnknownClientException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Непредвиденная ошибка: ", e.getMessage());
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleApplicationNotFoundException(ApplicationNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Ошибка введенных данных: ", e.getMessage());
    }

    @ExceptionHandler(InvalidSesCodeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleInvalidSesCodeException(InvalidSesCodeException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Доступ запрещен: ", e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Ошибка валидации: ", e.getMessage());
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

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        List<Violation> violations = e.getConstraintViolations().stream()
                                      .map(violation -> new Violation(violation.getPropertyPath().toString(),
                                              violation.getMessage()))
                                      .toList();
        return new ValidationErrorResponse(violations);
    }
}
