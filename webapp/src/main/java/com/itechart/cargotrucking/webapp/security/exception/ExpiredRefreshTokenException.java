package com.itechart.cargotrucking.webapp.security.exception;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException() {
        super("Refresh token not valid already");
    }
}
