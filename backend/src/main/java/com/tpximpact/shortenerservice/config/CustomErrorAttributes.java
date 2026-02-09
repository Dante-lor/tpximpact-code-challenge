package com.tpximpact.shortenerservice.config;

import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.tpximpact.shortenerservice.exception.NoSuchAliasException;
import com.tpximpact.shortenerservice.exception.ValidationFailedException;

/**
 * Override of the DefaultErrorAttributes. This hooks in to springs default error
 * handling and ensures that any expected client side errors have their error
 * messages returned.
 */
@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    /**
     * Overrides the default error attributes. This ensures an error message is sent back to the
     * user if validation fails or they provide an alias that doesn't exist. This will take priority
     * over the settings set in configuration.
     * 
     * @param webRequest the web request
     * @param the default configured options.
     */
    @Override
    public Map<String, Object> getErrorAttributes(
            WebRequest webRequest,
            ErrorAttributeOptions options) {

        
        final Throwable error = getError(webRequest);

        ErrorAttributeOptions finalOptions = switch (error) {
            case ValidationFailedException e -> options.including(Include.MESSAGE);
            case NoSuchAliasException e -> options.including(Include.MESSAGE);
            default -> options;
        };

        // Default attributes message to be included
        return super.getErrorAttributes(webRequest, finalOptions);

    }
}