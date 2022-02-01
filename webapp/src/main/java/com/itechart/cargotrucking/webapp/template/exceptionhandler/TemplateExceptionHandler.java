package com.itechart.cargotrucking.webapp.template.exceptionhandler;

import com.itechart.cargotrucking.core.scheduler.exception.CreateNewFileException;
import com.itechart.cargotrucking.core.scheduler.exception.FileReaderException;
import com.itechart.cargotrucking.core.scheduler.exception.FileWriterException;
import com.itechart.cargotrucking.core.scheduler.exception.GetResourceFileException;
import com.itechart.cargotrucking.core.scheduler.exception.InvalidTemplateException;
import com.itechart.cargotrucking.webapp.common.exceptionhandler.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class TemplateExceptionHandler {
    @ExceptionHandler(CreateNewFileException.class)
    protected ResponseEntity<Object> handleCreateNewFileException(RuntimeException e) {
        log.error("", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(FileWriterException.class)
    protected ResponseEntity<Object> handleFileWriterException(RuntimeException e) {
        log.error("", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(FileReaderException.class)
    protected ResponseEntity<Object> handleFileReaderException(RuntimeException e) {
        log.error("", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(GetResourceFileException.class)
    protected ResponseEntity<Object> handleGetResourceFileException(RuntimeException e) {
        log.error("", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }

    @ExceptionHandler(InvalidTemplateException.class)
    protected ResponseEntity<Object> handleInvalidTemplateException(RuntimeException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(ErrorResponse.buildFromSingleError(status, e.getMessage()));
    }
}
