package com.itechart.cargotrucking.core.security.exception;

public class DuplicatedRefreshTokenIdException extends RuntimeException {
    public DuplicatedRefreshTokenIdException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
