package com.itechart.cargotrucking.core.way.exception;

public class DuplicatedWaybillByInvoiceIdException extends RuntimeException {
    public DuplicatedWaybillByInvoiceIdException(String errorMessage, Object... args) {
        super(String.format(errorMessage, args));
    }
}
