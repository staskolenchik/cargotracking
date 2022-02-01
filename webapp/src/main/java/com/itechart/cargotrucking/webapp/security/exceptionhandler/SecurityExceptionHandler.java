package com.itechart.cargotrucking.webapp.security.exceptionhandler;

import com.itechart.cargotrucking.core.security.exception.DuplicatedRefreshTokenIdException;
import com.itechart.cargotrucking.core.security.exception.InvalidRefreshTokenException;
import com.itechart.cargotrucking.core.security.exception.RefreshTokenNotFoundException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import com.itechart.cargotrucking.webapp.security.exception.AccessException;
import com.itechart.cargotrucking.webapp.security.exception.ExpiredRefreshTokenException;
import com.itechart.cargotrucking.webapp.security.exception.InvalidJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class SecurityExceptionHandler {
    @ExceptionHandler(InvalidJwtException.class)
    protected ResponseEntity<Object> handleInvalidJwtExceptionException(RuntimeException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(DuplicatedRefreshTokenIdException.class)
    protected ResponseEntity<Object> handleDuplicatedRefreshTokenIdException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    protected ResponseEntity<Object> handleInvalidRefreshTokenException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    protected ResponseEntity<Object> handleRefreshTokenNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    protected ResponseEntity<Object> handleExpiredRefreshTokenException(RuntimeException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(AccessException.class)
    protected ResponseEntity<Object> handleAccessException(RuntimeException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
