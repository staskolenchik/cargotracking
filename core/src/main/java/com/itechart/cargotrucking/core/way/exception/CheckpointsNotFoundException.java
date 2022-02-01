package com.itechart.cargotrucking.core.way.exception;

public class CheckpointsNotFoundException extends RuntimeException {
    public CheckpointsNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
