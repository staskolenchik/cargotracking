package com.itechart.cargotrucking.core.product.exception;

public class ProductWriteoffNotFoundException extends RuntimeException {
    public ProductWriteoffNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
