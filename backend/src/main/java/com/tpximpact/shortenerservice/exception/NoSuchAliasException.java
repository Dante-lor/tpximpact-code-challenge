package com.tpximpact.shortenerservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchAliasException extends RuntimeException {
    
    public NoSuchAliasException(String message) {
        super(message);
    }
}
