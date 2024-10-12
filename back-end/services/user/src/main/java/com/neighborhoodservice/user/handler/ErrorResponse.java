package com.neighborhoodservice.user.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private Map<String, String> validationErrors;

    // Constructor for validation errors
    public ErrorResponse(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    // Constructor for general error messages
    public ErrorResponse(String message) {
        this.message = message;
    }

}