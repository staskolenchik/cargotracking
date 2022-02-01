package com.itechart.cargotrucking.core.scheduler.exception;

public class FileWriterException extends RuntimeException {
    public FileWriterException(String errorMessage, Object... args){
        super(String.format(errorMessage, args));
    }
}
