package com.tpximpact.shortenerservice.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tpximpact.shortenerservice.exception.NoSuchAliasException;
import com.tpximpact.shortenerservice.exception.ValidationFailedException;
import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ShortenResponse;
import com.tpximpact.shortenerservice.model.ShortenedAddress;
import com.tpximpact.shortenerservice.model.StoredAlias;
import com.tpximpact.shortenerservice.model.ValidationResult;
import com.tpximpact.shortenerservice.repository.ShortenedAddressDAO;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ShortenedAddressService {

    private final ShortenedAddressDAO shortenedAddressDAO;
    private final ShortenRequestValidationService requestValidation;
    private final Integer maxAliasSize;

    public ShortenedAddressService(
            ShortenedAddressDAO shortenedAddressDAO, 
            ShortenRequestValidationService shortenRequestValidationService,
            HttpServletRequest currentRequest,
            @Value("${alias.maxSize}") int maxAliasSize) {
        this.shortenedAddressDAO = shortenedAddressDAO;
        this.requestValidation = shortenRequestValidationService;
        this.maxAliasSize = maxAliasSize;

    }

    public ShortenResponse shorten(ShortenRequest shortenRequest) {
        ValidationResult result = requestValidation.validate(shortenRequest);

        if (result.isValid()) {
            
            final String alias = shortenRequest.customAlias() == null 
                ? generateNewAlias() 
                : shortenRequest.customAlias();

            final ShortenedAddress shortenedAddress = ShortenedAddress.builder()
                .originalUrl(shortenRequest.fullUrl().toString())
                .alias(alias)
                .build();

            final String savedAlias = shortenedAddressDAO.save(shortenedAddress).getAlias();
            return new ShortenResponse(toAbsoluteURL(savedAlias));
        } else {
            String errorMessage = "Request failed validation with the following errors: " +
                    String.join(", ", result.errors());

            throw new ValidationFailedException(errorMessage);
        }
    }

    public Optional<URI> getForwardedURI(String alias) {
        return shortenedAddressDAO.findByAlias(alias)
            .map(ShortenedAddress::getOriginalUrl)
            .map(this::toURI);
    }

    public void deleteStoredAlias(String alias) {
        final ShortenedAddress address = shortenedAddressDAO.findByAlias(alias)
            .orElseThrow(() -> new NoSuchAliasException("The alias " + alias + " does not exist"));

        shortenedAddressDAO.deleteById(address.getId());
    }

    public List<StoredAlias> getStoredURLs() {
        return shortenedAddressDAO.findAll().stream()
            .map(this::convertToStoredAlias)
            .toList();
    }

    private StoredAlias convertToStoredAlias(ShortenedAddress shortenedAddress) {
        return new StoredAlias(
                shortenedAddress.getAlias(), 
                toURI(shortenedAddress.getOriginalUrl()), 
                toAbsoluteURL(shortenedAddress.getAlias()));
    }

    private URI toAbsoluteURL(String alias) {
       return toURI(String.format("http://localhost:8080/%s", alias));
    }

    private URI toURI(String url) {
        return URI.create(url);
    }

    private String generateNewAlias() {
        // Generate new alias until one is not used
        boolean used = true;
        String generated = "";

        while (used) {
            generated = RandomStringUtils.secure()
                    .nextAlphanumeric(6, maxAliasSize);

            used = shortenedAddressDAO.findByAlias(generated).isPresent();
        }

        return generated;
    }
    
}
