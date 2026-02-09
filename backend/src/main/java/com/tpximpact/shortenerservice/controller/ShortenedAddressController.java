package com.tpximpact.shortenerservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ShortenResponse;
import com.tpximpact.shortenerservice.model.StoredAlias;
import com.tpximpact.shortenerservice.service.ShortenedAddressService;

/**
 * REST controller allowing access via HTTP to the service layer.
 */
@RestController
public class ShortenedAddressController {

    private final ShortenedAddressService shortenedAddressService;

    /**
     * Create a controller (called by spring dependency injection).
     *
     * @param shortenedAddressService shortenedAddressService
     */
    public ShortenedAddressController(ShortenedAddressService shortenedAddressService) {
        this.shortenedAddressService = shortenedAddressService;
    }

    /**
     * Shorten a URL. Takes a request and sends it to the service layer which performs validation
     * and execution.
     *
     * @param shortenRequest shorten request
     * @return the response if successful.
     */
    @PostMapping(path = "/shorten", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ShortenResponse shortenUrl(@RequestBody ShortenRequest shortenRequest) {
        return shortenedAddressService.shorten(shortenRequest);
    }
    
    /**
     * Get all aliases stored in the database.
     *
     * @return the aliases.
     */
    @GetMapping(path = "/urls", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StoredAlias> getStoredAliases() {
        return shortenedAddressService.getStoredURLs();
    }
    
    /**
     * Redirects a request to the full URL. Will match all paths except /urls as that's
     * required for {@link #getStoredAliases()}. If a match is found, the user will be
     * redirected to the full URL. If they are not found, a 404 response will be given.
     *
     * @param alias the alias (path variable)
     * @return redirect or not found request.
     */
    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/{alias:^(?!urls$)[a-zA-Z0-9_-]+$}")
    public ResponseEntity<Object> forwardToURL(@PathVariable("alias") String alias) {
        return shortenedAddressService.getForwardedURI(alias)
            .map(url -> {
                return ResponseEntity.status(HttpStatus.FOUND)
                    .location(url)
                    .build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Removes a shortened URL based on an alias.
     *
     * @param alias alias.
     */
    @DeleteMapping("/{alias}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlias(@PathVariable("alias") String alias) {
        shortenedAddressService.deleteStoredAlias(alias);
    }
}
