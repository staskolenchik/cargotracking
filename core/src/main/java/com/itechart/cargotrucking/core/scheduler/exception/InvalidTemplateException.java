package com.itechart.cargotrucking.core.scheduler.exception;

public class InvalidTemplateException extends RuntimeException {
    public InvalidTemplateException(String errorMessage, RuntimeException e) {
        super(errorMessage, e);
    }
}
