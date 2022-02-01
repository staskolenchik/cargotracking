package com.itechart.cargotrucking.webapp.invoice.exceptionhandler;

import com.itechart.cargotrucking.core.invoice.exception.EmptyProductListException;
import com.itechart.cargotrucking.core.invoice.exception.InvoiceNotFoundException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvoiceExceptionHandler {
    @ExceptionHandler(InvoiceNotFoundException.class)
    protected ResponseEntity<Object> handleInvoiceNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(EmptyProductListException.class)
    protected ResponseEntity<Object> handleEmptyProductListException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
