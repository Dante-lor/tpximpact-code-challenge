package com.tpximpact.shortenerservice.model;

import java.net.URI;

/**
 * Stored alias is the alias, full URL and short URL.
 * 
 * @param alias the alias
 * @param fullUrl the full URL
 * @param shortUrl the short URL
 */
public record StoredAlias(String alias, URI fullUrl, URI shortUrl) {
}