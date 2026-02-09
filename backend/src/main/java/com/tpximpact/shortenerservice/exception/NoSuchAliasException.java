package com.tpximpact.shortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception hrown when an alias is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchAliasException extends RuntimeException {
    
    /**
     * Creates the exception.
     *
     * @param message exception message.
     */
    public NoSuchAliasException(String message) {
        super(message);
    }
}
