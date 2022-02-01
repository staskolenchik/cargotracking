package com.itechart.cargotrucking.core.repair.exception;

public class NoSuchLinkException extends RuntimeException {
    public NoSuchLinkException(String errorMessage, Object... args){
       super(String.format(errorMessage, args));
    }
}
