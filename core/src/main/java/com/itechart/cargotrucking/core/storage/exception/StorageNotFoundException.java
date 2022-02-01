package com.itechart.cargotrucking.core.storage.exception;

public class StorageNotFoundException extends RuntimeException {
    public StorageNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
