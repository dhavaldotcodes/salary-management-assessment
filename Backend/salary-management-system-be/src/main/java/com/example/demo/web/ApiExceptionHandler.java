package com.example.demo.web;

import com.example.demo.domain.UnknownCurrencyException;
import com.example.demo.employee.DuplicateEmailException;
import com.example.demo.employee.EmployeeNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiError> notFound(EmployeeNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiError> conflict(DuplicateEmailException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), Map.of("email", ex.getMessage()));
    }

    @ExceptionHandler(UnknownCurrencyException.class)
    public ResponseEntity<ApiError> badCurrency(UnknownCurrencyException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of("currency", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidBody(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Validation failed", fields);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> invalidParam(ConstraintViolationException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of());
    }

    private static ResponseEntity<ApiError> error(HttpStatus status, String message, Map<String, String> fields) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                fields
        );
        return ResponseEntity.status(status).body(body);
    }
}
