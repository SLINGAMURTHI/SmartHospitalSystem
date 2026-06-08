package com.hospital;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Automatically catches any IllegalStateException thrown anywhere in the app
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalState(IllegalStateException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Automatically catches any RuntimeException (like Slot Not Found)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntime(RuntimeException ex) {
        ApiResponse response = new ApiResponse(ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}