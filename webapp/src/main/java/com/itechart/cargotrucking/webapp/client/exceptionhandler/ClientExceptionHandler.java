package com.itechart.cargotrucking.webapp.client.exceptionhandler;

import com.itechart.cargotrucking.core.client.exception.ClientBadCredentialException;
import com.itechart.cargotrucking.core.client.exception.ClientNotFoundException;
import com.itechart.cargotrucking.core.client.exception.DuplicatedClientNameException;
import com.itechart.cargotrucking.core.client.exception.InvalidDateException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ClientExceptionHandler {
    @ExceptionHandler(DuplicatedClientNameException.class)
    protected ResponseEntity<Object> handleDuplicatedClientNameException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(ClientNotFoundException.class)
    protected ResponseEntity<Object> handleClientNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(ClientBadCredentialException.class)
    protected ResponseEntity<Object> handleClientBadCredentialException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(InvalidDateException.class)
    protected ResponseEntity<Object> handleInvalidDateException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

}
