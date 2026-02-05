package com.tpximpact.shortenerservice.config;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.tpximpact.shortenerservice.exception.NoSuchAliasException;
import com.tpximpact.shortenerservice.exception.ValidationFailedException;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(
            WebRequest webRequest,
            ErrorAttributeOptions options) {

        
        Throwable error = getError(webRequest);

        ErrorAttributeOptions finalOptions = switch (error) {
            case ValidationFailedException e -> options.including(Include.MESSAGE);
            case NoSuchAliasException e -> options.including(Include.MESSAGE);
            default -> options;
        };

        // Default attributes message to be included
        return super.getErrorAttributes(webRequest, finalOptions);

    }
}