package com.itechart.cargotrucking.core.productowner.exception;

public class ProductOwnerNotFoundException extends RuntimeException {
    public ProductOwnerNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
