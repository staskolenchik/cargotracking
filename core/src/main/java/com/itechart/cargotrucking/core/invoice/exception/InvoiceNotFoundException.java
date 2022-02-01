package com.itechart.cargotrucking.core.invoice.exception;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
