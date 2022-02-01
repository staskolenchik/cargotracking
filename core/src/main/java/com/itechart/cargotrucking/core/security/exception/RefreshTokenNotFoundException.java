package com.itechart.cargotrucking.core.security.exception;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
