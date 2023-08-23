package com.neoflex.application.controller.advice;

import com.neoflex.application.dto.error.ErrorResponse;
import com.neoflex.application.dto.error.ValidationErrorResponse;
import com.neoflex.application.dto.error.Violation;
import com.neoflex.application.exception.BadRequestException;
import com.neoflex.application.exception.NotCompletedImplementationException;
import com.neoflex.application.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Ошибка введенных данных: ", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Ошибка введенных данных: ", e.getMessage());
    }

    @ExceptionHandler(NotCompletedImplementationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notCompletedComponentImplementation(NotCompletedImplementationException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse("Реализация не завершена: ", e.getMessage());
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
