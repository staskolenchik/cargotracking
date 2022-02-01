package com.itechart.cargotrucking.core.user.exception;

public class UserBadCredentialsException extends RuntimeException {
    public UserBadCredentialsException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }

    public UserBadCredentialsException(Throwable cause, String errorMessage, Object... args) {
        super(String.format(errorMessage, args), cause);
    }
}
