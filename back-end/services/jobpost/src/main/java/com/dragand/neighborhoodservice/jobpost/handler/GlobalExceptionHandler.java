package com.dragand.neighborhoodservice.jobpost.handler;



import com.dragand.neighborhoodservice.jobpost.exception.AuthorizationException;
import com.dragand.neighborhoodservice.jobpost.exception.ResourceAlreadyExistsException;
import com.dragand.neighborhoodservice.jobpost.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                getResponseStatus(ex).value(),
                ex.getMessage(),
                "Resource not found"
        );
        log.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, getResponseStatus(ex));

    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                getResponseStatus(ex).value(),
                ex.getMessage(),
                "Resource already exists"
        );
        log.error(ex.getMessage());
        return new ResponseEntity<>(errorResponse, getResponseStatus(ex));

    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException ex) {
        log.error(ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage(),
                "Authorization error"
        );
        return new ResponseEntity<>(errorResponse, getResponseStatus(ex));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            var fieldName = ((FieldError)error).getField();
            var errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                getResponseStatus(ex).value(),
                ex.getMessage(),
                "Invalid request",
                errors
        );

        return new ResponseEntity<>(errorResponse, getResponseStatus(ex));

    }

    /**
     * Helper method to get the response status from the exception
     * @param ex the exception
     * @return the response status
     */
    private HttpStatus getResponseStatus(Exception ex) {
        ResponseStatus responseStatus = ex.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
