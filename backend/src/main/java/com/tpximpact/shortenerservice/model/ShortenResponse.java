package com.tpximpact.shortenerservice.model;

import java.net.URI;

/**
 * Wrapper object for the shortend URL.
 *
 * @param shortURL the short URL
 */
public record ShortenResponse(URI shortUrl) {
    
}
