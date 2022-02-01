package com.itechart.cargotrucking.core.user.exception;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
