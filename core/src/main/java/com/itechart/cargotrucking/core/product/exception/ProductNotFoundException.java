package com.itechart.cargotrucking.core.product.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
