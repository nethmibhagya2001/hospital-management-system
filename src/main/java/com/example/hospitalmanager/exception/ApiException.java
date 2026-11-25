package com.example.hospitalmanager.exception;

import java.util.Objects;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = Objects.requireNonNull(status, "status");
    }

    public HttpStatus getStatus() {
        return status;
    }
}
