package com.itechart.cargotrucking.core.scheduler.exception;

public class FileReaderException extends RuntimeException {
    public FileReaderException(String errorMessage, Object... args){
        super(String.format(errorMessage, args));
    }
}
