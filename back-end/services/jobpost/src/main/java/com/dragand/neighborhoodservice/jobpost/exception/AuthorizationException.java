package com.dragand.neighborhoodservice.jobpost.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = org.springframework.http.HttpStatus.FORBIDDEN)
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }

}
