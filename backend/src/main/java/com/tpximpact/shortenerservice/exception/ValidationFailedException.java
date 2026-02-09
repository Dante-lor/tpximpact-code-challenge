package com.tpximpact.shortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a request fails validation.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationFailedException extends RuntimeException {

    /**
     * Creates the exception.
     *
     * @param message exception message.
     */
    public ValidationFailedException(String message) {
        super(message);
    }
    
}
