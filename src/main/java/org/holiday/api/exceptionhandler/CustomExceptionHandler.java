package org.holiday.api.exceptionhandler;

import org.holiday.exception.DayOffPerYearNotInitializedException;
import org.holiday.exception.NoDayOffAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DayOffPerYearNotInitializedException.class)
    public ResponseEntity<?> handleException(DayOffPerYearNotInitializedException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(NoDayOffAvailableException.class)
    public ResponseEntity<?> handleException(NoDayOffAvailableException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", exception.getMessage()));
    }
}
