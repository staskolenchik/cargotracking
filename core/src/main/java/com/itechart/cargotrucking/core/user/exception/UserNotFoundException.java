package com.itechart.cargotrucking.core.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
