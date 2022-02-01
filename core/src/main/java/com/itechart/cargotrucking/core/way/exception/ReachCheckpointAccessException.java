package com.itechart.cargotrucking.core.way.exception;

public class ReachCheckpointAccessException extends RuntimeException {
    public ReachCheckpointAccessException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
