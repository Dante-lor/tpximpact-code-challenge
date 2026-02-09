package com.tpximpact.shortenerservice.service;

import java.net.URI;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Business logic for finding the URL that's being requested. This means that if this was 
 * deployed and accessed via a DNS address, the DNS address would be returned rather than
 * localhost.
 */
@Service
public class CurrentURLService {

    private final HttpServletRequest currentRequest;

    /**
     * Creates the service.
     * @param currentRequest threadlocal proxy to the current request
     */
    public CurrentURLService(HttpServletRequest currentRequest) {
        this.currentRequest = currentRequest;
    }

    /**
     * Get the current URL with no path attached. If the default port is used to access this service,
     * no port will appear on the URL.
     *
     * @return the requested URL.
     */
    public String getRequestedURLWithNoPath() {
        
        final StringBuffer requestedURL = currentRequest.getRequestURL();

        final URI uri = URI.create(requestedURL.toString());

        return uri.getPort() == -1 ?
            String.format("%s://%s", uri.getScheme(), uri.getHost())
            : 
            String.format("%s://%s:%s", uri.getScheme(), uri.getHost(), uri.getPort());
    }
    
}
