package org.holiday.api.exceptionhandler;

import org.holiday.exception.DayOffPerYearNotInitializedException;
import org.holiday.exception.NoDayOffAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * use some JDK16 feature :)
     */
    record Message(String message) {
    }

    @ExceptionHandler(DayOffPerYearNotInitializedException.class)
    public ResponseEntity<?> handleException(DayOffPerYearNotInitializedException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Message(exception.getMessage()));
    }

    @ExceptionHandler(NoDayOffAvailableException.class)
    public ResponseEntity<?> handleException(NoDayOffAvailableException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Message(exception.getMessage()));
    }
}
