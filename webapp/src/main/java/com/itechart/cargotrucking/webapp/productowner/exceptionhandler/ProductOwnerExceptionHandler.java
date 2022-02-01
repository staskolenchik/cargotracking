package com.itechart.cargotrucking.webapp.productowner.exceptionhandler;

import com.itechart.cargotrucking.core.productowner.exception.ProductOwnerNotFoundException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductOwnerExceptionHandler {
    @ExceptionHandler(ProductOwnerNotFoundException.class)
    protected ResponseEntity<Object> handleProductOwnerNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
