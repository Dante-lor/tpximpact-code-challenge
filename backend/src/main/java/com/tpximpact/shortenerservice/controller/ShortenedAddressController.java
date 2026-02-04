package com.tpximpact.shortenerservice.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ShortenResponse;
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
}
