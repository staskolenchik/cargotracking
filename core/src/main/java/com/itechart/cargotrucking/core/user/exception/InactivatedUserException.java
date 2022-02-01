package com.itechart.cargotrucking.core.user.exception;

public class InactivatedUserException extends RuntimeException {
    public InactivatedUserException(String errorMessage, Object... args){
        String.format(errorMessage, args);
    }
}
