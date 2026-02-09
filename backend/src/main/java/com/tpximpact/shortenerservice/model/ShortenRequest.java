package com.tpximpact.shortenerservice.model;

import java.net.URI;

import jakarta.annotation.Nullable;

/**
 * Shorten request which includes full URL and custom alias.
 *
 * @param fullUrl full URL
 * @param customAlias custom alias (or null)
 */
public record ShortenRequest(URI fullUrl, @Nullable String customAlias) {
    
}
