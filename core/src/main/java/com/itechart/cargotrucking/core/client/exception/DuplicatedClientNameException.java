package com.itechart.cargotrucking.core.client.exception;

public class DuplicatedClientNameException extends RuntimeException {
    public DuplicatedClientNameException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
