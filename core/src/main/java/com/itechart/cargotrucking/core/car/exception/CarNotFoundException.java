package com.itechart.cargotrucking.core.car.exception;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
