package com.neighborhoodservice.user.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErrorResponse {

    LocalDateTime timestamp;
    int status;
    String error;
    String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude this field if it's null
    private Map<String, String> validationErrors;

    public ErrorResponse(int status, String error, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // Constructor for validation errors
    public ErrorResponse(int status, String error, String message, Map<String, String> validationErrors) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.validationErrors = validationErrors;  // Only included when validation errors exist
    }

}