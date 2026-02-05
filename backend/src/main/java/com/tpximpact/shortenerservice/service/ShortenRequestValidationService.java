package com.tpximpact.shortenerservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ValidationResult;
import com.tpximpact.shortenerservice.repository.ShortenedAddressDAO;

@Service
public class ShortenRequestValidationService {

    private final int maxAliasSize;
    private final ShortenedAddressDAO shortenedAddressDAO;

    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile("[a-z-\\d]+");

    public ShortenRequestValidationService(@Value("alias.maxSize") int maxAliasSize, 
        ShortenedAddressDAO shortenedAddressDAO) {
        this.maxAliasSize = maxAliasSize;
        this.shortenedAddressDAO = shortenedAddressDAO;
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
                    errors.add("the max size for any alias is " + maxAliasSize + " characters");
                }

                if (customAlias.isBlank()) {
                    errors.add("aliases cannot be blank");
                }

                // Only need to check for duplicates if custom alias is valid (and therefore could exist in DB)
                if (errors.size() == 0 && alreadyExists(customAlias)) {
                    errors.add("the alias " + customAlias + " is already mapped to a URL");
                }
            }

            if (shortenRequest.fullUrl() == null) {
                errors.add("full url must be provided");
            } 

            // TODO continue adding validation (like checking it's not one of our other paths)


        }

        return new ValidationResult(errors);
    }

    private boolean alreadyExists(String alias) {
        return shortenedAddressDAO.findByAlias(alias).isPresent();
    }
}
