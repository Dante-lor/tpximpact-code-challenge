package com.tpximpact.shortenerservice.model;

import java.net.URI;

public record ShortenRequest(URI fullUrl, String customAlias) {
    
}
