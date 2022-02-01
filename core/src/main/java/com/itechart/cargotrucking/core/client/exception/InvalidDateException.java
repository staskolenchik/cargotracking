package com.itechart.cargotrucking.core.client.exception;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
