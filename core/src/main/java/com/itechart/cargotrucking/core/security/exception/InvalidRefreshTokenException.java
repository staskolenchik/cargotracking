package com.itechart.cargotrucking.core.security.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Jwt must have 3 blocks");
    }
}
