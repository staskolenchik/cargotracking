package com.itechart.cargotrucking.core.scheduler.exception;

public class GetResourceFileException extends RuntimeException {
    public GetResourceFileException(String errorMessage, Object... args){
        super(String.format(errorMessage, args));
    }
}
