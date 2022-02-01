package com.itechart.cargotrucking.webapp.way.exceptionhandler;

import com.itechart.cargotrucking.core.way.exception.CheckpointsNotFoundException;
import com.itechart.cargotrucking.core.way.exception.DuplicatedWaybillByInvoiceIdException;
import com.itechart.cargotrucking.core.way.exception.ReachCheckpointAccessException;
import com.itechart.cargotrucking.core.way.exception.WaybillNotFoundException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WaybillExceptionHandler {
    @ExceptionHandler(DuplicatedWaybillByInvoiceIdException.class)
    protected ResponseEntity<Object> handleDuplicatedWaybillByInvoiceIdException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(WaybillNotFoundException.class)
    protected ResponseEntity<Object> handleWaybillNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(CheckpointsNotFoundException.class)
    protected ResponseEntity<Object> handleCheckpointsNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(ReachCheckpointAccessException.class)
    protected ResponseEntity<Object> handleReachCheckpointAccessException(RuntimeException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
