package com.tpximpact.shortenerservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class CurrentURLServiceTest {

    @Mock
    HttpServletRequest httpServletRequest;

    @InjectMocks
    CurrentURLService currentURLService;

    @ParameterizedTest
    @ValueSource(strings = { "/something", "?myquery=here", "/something?myQuery=Here", "/trailing/slash/"})
    void shouldIgnoreQueryParametersAndPaths(String suffix) {
        // Given
        String url = "http://localhost" + suffix;
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(url));

        // When
        String noPath = currentURLService.getRequestedURLWithNoPath();

        // Then
        assertEquals("http://localhost", noPath);

    }

    @Test
    void shouldWorkForIPAddresses() {
        // Given
        String url = "http://192.168.0.2/abcd";
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(url));

        // When
        String noPath = currentURLService.getRequestedURLWithNoPath();

        // Then
        assertEquals("http://192.168.0.2", noPath);
    }

    @Test
    void shouldIncludeNonStandardPorts() {
        String url = "http://example.com:8080";
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(url));

        // When
        String noPath = currentURLService.getRequestedURLWithNoPath();

        // Then
        assertEquals("http://example.com:8080", noPath);
    }

    @Test
    void shouldUseRelevantScheme() {
        String url = "https://example.com/";
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(url));

        // When
        String noPath = currentURLService.getRequestedURLWithNoPath();

        // Then
        assertEquals("https://example.com", noPath);
    }
}
