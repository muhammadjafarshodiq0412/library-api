package com.jafarshodiq.library.config;

import com.jafarshodiq.library.dto.ErrorResponse;
import com.jafarshodiq.library.exception.ConflictException;
import com.jafarshodiq.library.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> notFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(ConflictException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> conflict(ConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErrorResponse.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErrorResponse.FieldViolation(e.getField(), e.getDefaultMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Request validation failed", req, violations);
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<ErrorResponse> unexpected(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal error", req, List.of());
    }

    private org.springframework.http.ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                                          HttpServletRequest req,
                                                                          List<ErrorResponse.FieldViolation> violations) {
        return org.springframework.http.ResponseEntity.status(status).body(
                new ErrorResponse(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, req.getRequestURI(), violations));
    }
}
