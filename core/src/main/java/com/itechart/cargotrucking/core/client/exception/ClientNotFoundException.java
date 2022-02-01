package com.itechart.cargotrucking.core.client.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
