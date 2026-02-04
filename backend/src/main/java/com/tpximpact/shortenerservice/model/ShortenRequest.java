package com.tpximpact.shortenerservice.model;

import java.net.URL;

public record ShortenRequest(URL fullUrl, String customAlias) {
    
}
