package com.itechart.cargotrucking.core.way.exception;

public class WaybillNotFoundException extends RuntimeException {
    public WaybillNotFoundException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
