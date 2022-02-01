package com.itechart.cargotrucking.core.user.exception;

public class DuplicatedLoginException extends RuntimeException {
    public DuplicatedLoginException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
