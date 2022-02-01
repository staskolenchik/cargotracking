package com.itechart.cargotrucking.core.report.exception;

public class InvalidDateIntervalException extends RuntimeException {
    public InvalidDateIntervalException() {
        super("Initial date cannot be greater than final date");
    }
}
