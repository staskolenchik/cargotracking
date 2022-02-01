package com.itechart.cargotrucking.core.invoice.exception;

public class EmptyProductListException extends RuntimeException {
    public EmptyProductListException() {
        super("Product list cannot be empty");
    }
}
