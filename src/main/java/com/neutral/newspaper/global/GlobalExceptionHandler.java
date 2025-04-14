package com.neutral.newspaper.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
        log.error("CustomException: {}", exception.getMessage());
        ErrorType errorType = exception.getErrorType();
        ErrorResponse response = new ErrorResponse(errorType.getCode(), errorType.getMessage());
        return ResponseEntity.status(errorType.getHttpStatus()).body(response);
    }
}
