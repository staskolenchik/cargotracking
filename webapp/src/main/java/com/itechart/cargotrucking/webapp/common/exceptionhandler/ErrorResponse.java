package com.itechart.cargotrucking.webapp.common.exceptionhandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private List<String> errors;

    private ErrorResponse(HttpStatus status) {
        timestamp = LocalDateTime.now();
        this.status = status.value();
    }

    static ErrorResponse buildFromErrorList(HttpStatus status, List<String> errors) {
        ErrorResponse errorResponse = new ErrorResponse(status);
        errorResponse.setErrors(errors);

        return errorResponse;
    }

    public static ErrorResponse buildFromSingleError(HttpStatus status, String error) {
        ErrorResponse errorResponse = new ErrorResponse(status);
        errorResponse.setErrors(Collections.singletonList(error));

        return errorResponse;
    }
}
