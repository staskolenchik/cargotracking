package com.itechart.cargotrucking.webapp.email.exceptionhandler;

import com.itechart.cargotrucking.core.user.exception.EmailNotFoundException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EmailExceptionHandler {
    @ExceptionHandler(EmailNotFoundException.class)
    protected ResponseEntity<Object> handleEmailNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
