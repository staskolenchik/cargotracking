package com.itechart.cargotrucking.webapp.user.exceptionhandler;

import com.itechart.cargotrucking.core.repair.exception.NoSuchLinkException;
import com.itechart.cargotrucking.core.user.exception.DuplicatedEmailException;
import com.itechart.cargotrucking.core.user.exception.DuplicatedLoginException;
import com.itechart.cargotrucking.core.user.exception.EmptyUserRolesException;
import com.itechart.cargotrucking.core.user.exception.InactivatedUserException;
import com.itechart.cargotrucking.core.user.exception.UserBadCredentialsException;
import com.itechart.cargotrucking.core.user.exception.UserNotFoundException;
import com.itechart.cargotrucking.core.userprofile.exception.UUIDNotFoundException;
import com.itechart.cargotrucking.webapp.user.exception.PasswordsNotEqualsException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(DuplicatedLoginException.class)
    protected ResponseEntity<Object> handleDuplicatedLoginException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity<Object> handleUserNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(UserBadCredentialsException.class)
    protected ResponseEntity<Object> handleUserBadCredentialsException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(EmptyUserRolesException.class)
    protected ResponseEntity<Object> handleEmptyUserRolesException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(InactivatedUserException.class)
    protected ResponseEntity<Object> handleInactivatedUserException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(NoSuchLinkException.class)
    protected ResponseEntity<Object> handleNoSuchLinkException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(PasswordsNotEqualsException.class)
    protected ResponseEntity<Object> handlePasswordsNotEqualsException(RuntimeException e) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    protected ResponseEntity<Object> handleDuplicatedEmailException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(UUIDNotFoundException.class)
    protected ResponseEntity<Object> handleUUIDNotFoundException(RuntimeException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
