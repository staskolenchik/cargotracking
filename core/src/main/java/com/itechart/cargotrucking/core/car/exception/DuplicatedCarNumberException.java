package com.itechart.cargotrucking.core.car.exception;

public class DuplicatedCarNumberException extends RuntimeException {
    public DuplicatedCarNumberException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
