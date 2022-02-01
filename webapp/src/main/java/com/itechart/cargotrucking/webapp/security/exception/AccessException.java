package com.itechart.cargotrucking.webapp.security.exception;

public class AccessException extends RuntimeException {
    public AccessException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
