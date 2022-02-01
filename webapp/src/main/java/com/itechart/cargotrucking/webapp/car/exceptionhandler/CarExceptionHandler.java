package com.itechart.cargotrucking.webapp.car.exceptionhandler;

import com.itechart.cargotrucking.core.car.exception.CarNotFoundException;
import com.itechart.cargotrucking.core.car.exception.DuplicatedCarNumberException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CarExceptionHandler {
    @ExceptionHandler(CarNotFoundException.class)
    protected ResponseEntity<Object> handleCarNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(DuplicatedCarNumberException.class)
    protected ResponseEntity<Object> handleClientNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
