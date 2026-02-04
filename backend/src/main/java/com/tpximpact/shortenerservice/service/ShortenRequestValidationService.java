package com.tpximpact.shortenerservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ValidationResult;

@Service
public class ShortenRequestValidationService {

    private final int maxAliasSize;

    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("[a-z-]+");

    public ShortenRequestValidationService(@Value("alias.maxSize") int maxAliasSize) {
        this.maxAliasSize = maxAliasSize;
    }

    public ValidationResult validate(final ShortenRequest shortenRequest) {
        final List<String> errors = new ArrayList<>();
        if (shortenRequest == null) {
            errors.add("request cannot be null");
        } else {
            if (shortenRequest.customAlias() != null) {
                final String customAlias = shortenRequest.customAlias();

                if (!ALLOWED_CHARACTERS.matcher(customAlias).matches()) {
                    errors.add("alias must only contain lowercase letters and dashes");
                }

                if (customAlias.length() > maxAliasSize) {
                    errors.add("The max size for any alias is " + maxAliasSize + " characters");
                }
            }

            if (shortenRequest.fullUrl() == null) {
                errors.add("full url must be provided");
            } 

            // TODO continue adding validation (like checking it's not one of our other paths)


        }

        return new ValidationResult(errors);


    }
}
