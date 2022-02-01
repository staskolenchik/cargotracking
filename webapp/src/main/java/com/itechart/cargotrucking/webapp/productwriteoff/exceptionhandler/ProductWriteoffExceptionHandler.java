package com.itechart.cargotrucking.webapp.productwriteoff.exceptionhandler;

import com.itechart.cargotrucking.core.product.exception.ProductWriteoffNotFoundException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductWriteoffExceptionHandler {
    @ExceptionHandler(ProductWriteoffNotFoundException.class)
    protected ResponseEntity<Object> handleProductWriteoffNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
