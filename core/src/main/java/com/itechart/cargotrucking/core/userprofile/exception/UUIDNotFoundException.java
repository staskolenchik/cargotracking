package com.itechart.cargotrucking.core.userprofile.exception;

public class UUIDNotFoundException extends RuntimeException {
    public UUIDNotFoundException() {
        super("Unique identifier is not valid");
    }
}
