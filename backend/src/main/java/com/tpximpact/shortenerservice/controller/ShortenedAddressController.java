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

@RestController
public class ShortenedAddressController {

    private final ShortenedAddressService shortenedAddressService;

    public ShortenedAddressController(ShortenedAddressService shortenedAddressService) {
        this.shortenedAddressService = shortenedAddressService;
    }

    @PostMapping(path = "/shorten", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ShortenResponse shortenUrl(@RequestBody ShortenRequest shortenRequest) {
        return shortenedAddressService.shorten(shortenRequest);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @GetMapping("/{alias}")
    public ResponseEntity<Object> forwardToURL(@PathVariable("alias") String alias) {
        return shortenedAddressService.getForwardedURI(alias)
            .map(url -> {
                return ResponseEntity.status(HttpStatus.FOUND)
                .location(url)
                .build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{alias}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlias(@PathVariable("alias") String alias) {
        shortenedAddressService.deleteStoredAlias(alias);
    }

    @GetMapping(path = "/urls", 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<StoredAlias> getStoredAliases() {
        return shortenedAddressService.getStoredURLs();
    }



}
