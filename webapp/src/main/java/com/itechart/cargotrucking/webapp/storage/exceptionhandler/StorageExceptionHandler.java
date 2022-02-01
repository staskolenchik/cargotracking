package com.itechart.cargotrucking.webapp.storage.exceptionhandler;

import com.itechart.cargotrucking.core.storage.exception.StorageNotFoundException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StorageExceptionHandler {
    @ExceptionHandler(StorageNotFoundException.class)
    protected ResponseEntity<Object> handleStorageNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
