package com.itechart.cargotrucking.core.user.exception;

public class EmptyUserRolesException extends RuntimeException {
    public EmptyUserRolesException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
