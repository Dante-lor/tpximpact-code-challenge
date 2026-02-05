package com.tpximpact.shortenerservice.model;

import java.net.URI;

public record StoredAlias(String alias, URI fullUrl, URI shortUrl) {
}