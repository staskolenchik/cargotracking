package com.itechart.cargotrucking.core.client.exception;

public class ClientBadCredentialException extends RuntimeException {
    public ClientBadCredentialException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
