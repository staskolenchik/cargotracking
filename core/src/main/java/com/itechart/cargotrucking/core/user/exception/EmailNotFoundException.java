package com.itechart.cargotrucking.core.user.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
