package com.example.booking.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError e : ex.getBindingResult().getFieldErrors())
            errors.put(e.getField(), e.getDefaultMessage());
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req.getRequestURI(), errors);
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    ResponseEntity<ErrorResponse> badRequest(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> resourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    ResponseEntity<ErrorResponse> reservationNotFound(ReservationNotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(ConflictException.class)
    ResponseEntity<ErrorResponse> conflict(ConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> credentials(BadCredentialsException ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "Invalid username or password", req.getRequestURI(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> denied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI(), null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path, Map<String,String> errors) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message, path, LocalDateTime.now(), errors));
    }

    public record ErrorResponse(int status, String message, String path,
                                LocalDateTime timestamp, Map<String,String> errors) {}
}
