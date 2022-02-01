package com.itechart.cargotrucking.webapp.security.exception;

public class InvalidJwtException extends RuntimeException {
    public InvalidJwtException() {
        super("Invalid JWT");
    }

    public InvalidJwtException(Throwable e) {
        super("Invalid JWT", e);
    }
}
