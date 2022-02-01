package com.itechart.cargotrucking.core.scheduler.exception;

public class CreateNewFileException extends RuntimeException {
    public CreateNewFileException(String errorMessage, Object... args){
        super(String.format(errorMessage, args));
    }
}
