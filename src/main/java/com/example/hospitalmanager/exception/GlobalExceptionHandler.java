package com.example.hospitalmanager.exception;

import com.example.hospitalmanager.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            ApiException exception, HttpServletRequest request) {
        return createResponse(exception.getStatus(), exception.getMessage(), request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(
            Exception exception, HttpServletRequest request) {
        String message;
        if (exception instanceof MethodArgumentNotValidException validationException) {
            message = validationException.getBindingResult().getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
        } else {
            ConstraintViolationException validationException = (ConstraintViolationException) exception;
            message = validationException.getConstraintViolations().stream()
                    .map(violation -> violation.getMessage())
                    .collect(Collectors.joining(", "));
        }
        return createResponse(HttpStatus.BAD_REQUEST,
                message.isBlank() ? "Validation failed" : message, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException exception, HttpServletRequest request) {
        return createResponse(HttpStatus.UNAUTHORIZED, "Authentication required", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException exception, HttpServletRequest request) {
        return createResponse(HttpStatus.FORBIDDEN, "Access denied", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception, HttpServletRequest request) {
        return createResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponse> createResponse(
            HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(response);
    }
}
