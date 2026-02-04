package com.tpximpact.shortenerservice.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.springframework.stereotype.Service;

import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ShortenResponse;
import com.tpximpact.shortenerservice.model.ShortenedAddress;
import com.tpximpact.shortenerservice.model.ValidationResult;
import com.tpximpact.shortenerservice.repository.ShortenedAddressDAO;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ShortenedAddressService {

    private final ShortenedAddressDAO shortenedAddressDAO;
    private final ShortenRequestValidationService requestValidation;

    public ShortenedAddressService(ShortenedAddressDAO shortenedAddressDAO, 
            ShortenRequestValidationService shortenRequestValidationService,
            HttpServletRequest currentRequest) {
        this.shortenedAddressDAO = shortenedAddressDAO;
        this.requestValidation = shortenRequestValidationService;
    }

    public ShortenResponse shorten(ShortenRequest shortenRequest) {
        ValidationResult result = requestValidation.validate(shortenRequest);

        if (result.isValid()) {
            final ShortenedAddress shortenedAddress = ShortenedAddress.builder()
                .originalUrl(shortenRequest.fullUrl().toString())
                .alias(shortenRequest.customAlias())
                .build();

            // TODO need to check if alias exists (if defined) or generate a new one 

            final String savedAlias = shortenedAddressDAO.save(shortenedAddress).getAlias();
            return new ShortenResponse(toAbsoluteURL(savedAlias));
        } else {
            String errorMessage = "Request failed validation with the following errors: " +
                    String.join(", ", result.errors());

            throw new IllegalArgumentException(errorMessage);
        }
    }

    private URL toAbsoluteURL(String alias) {
        try {
            return URI.create(String.format("http://localhost:8080/%s", alias)).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } 
    }
    
}
