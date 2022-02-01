package com.itechart.cargotrucking.webapp.user.exception;

public class PasswordsNotEqualsException extends RuntimeException {
    public PasswordsNotEqualsException() {
        super("Passwords are not equal");
    }
}
