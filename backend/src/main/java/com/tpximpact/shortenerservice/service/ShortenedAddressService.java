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

/**
 * ShortenedAddressService manages storage and retrieval of shortened URL addresses.
 */
@Service
public class ShortenedAddressService {

    private final ShortenedAddressDAO shortenedAddressDAO;
    private final ShortenRequestValidationService requestValidation;
    private final CurrentURLService currentURLService;
    private final Integer maxAliasSize;

    /**
     * Creates an instance of the service.
     *
     * @param shortenedAddressDAO the persistence layer
     * @param shortenRequestValidationService validation service
     * @param currentURLService service for getting the reuqested URL
     * @param maxAliasSize max size of an alias.
     */
    public ShortenedAddressService(
            ShortenedAddressDAO shortenedAddressDAO, 
            ShortenRequestValidationService shortenRequestValidationService,
            CurrentURLService currentURLService,
            @Value("${alias.maxSize}") int maxAliasSize) {
        this.shortenedAddressDAO = shortenedAddressDAO;
        this.requestValidation = shortenRequestValidationService;
        this.currentURLService = currentURLService;
        this.maxAliasSize = maxAliasSize;
    }

    /**
     * Shortens a URL using the URL request. Hands of validation to an external service
     * before interfacing with the persistence layer. If no alias is provided, one will
     * be generated randomly.
     *
     * @param shortenRequest shorten request
     * @return the shortened URL.
     */
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

    /**
     * Returns an optional containing the forwarded URL for any given alias. If the alias
     * is not found in the persistence layer, the optional will be empty.
     *
     * @param alias the alias
     * @return an optional containing the original URL (if present).
     */
    public Optional<URI> getForwardedURI(String alias) {
        return shortenedAddressDAO.findByAlias(alias)
            .map(ShortenedAddress::getOriginalUrl)
            .map(this::toURI);
    }

    /**
     * Deletes the stored alias from the persistence layer. If no value with the provided alias
     * exists, an exception will be thrown.
     *
     * @param alias the alias to delete.
     */
    public void deleteStoredAlias(String alias) {
        final ShortenedAddress address = shortenedAddressDAO.findByAlias(alias)
            .orElseThrow(() -> new NoSuchAliasException("The alias " + alias + " does not exist"));

        shortenedAddressDAO.deleteById(address.getId());
    }

    /**
     * Get all the stored URLs in the system.
     *
     * @return the stored aliases.
     */
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
       return toURI(String.format("%s/%s", currentURLService.getRequestedURLWithNoPath(), alias));
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
