package com.tpximpact.shortenerservice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ShortenResponse;
import com.tpximpact.shortenerservice.model.StoredAlias;
import com.tpximpact.shortenerservice.service.ShortenedAddressService;

@ExtendWith(MockitoExtension.class)
public class ShortenedAddressControllerTest {

    @Mock
    private ShortenedAddressService shortenedAddressService;

    @InjectMocks
    private ShortenedAddressController controller;

    @Test
    void test_shortenUrl_returnsShortenResponseWhenServiceReturns() {
        ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), null);
        ShortenResponse resp = new ShortenResponse(URI.create("http://short/abc"));

        when(shortenedAddressService.shorten(req)).thenReturn(resp);

        ShortenResponse result = controller.shortenUrl(req);

        assertEquals(resp, result);
        verify(shortenedAddressService).shorten(req);
    }

    @Test
    void test_forwardToURL_redirectsToLocationWhenAliasFound() {
        String alias = "abc";
        URI target = URI.create("http://example.com");

        when(shortenedAddressService.getForwardedURI(alias)).thenReturn(Optional.of(target));

        ResponseEntity<Object> resp = controller.forwardToURL(alias);

        assertEquals(HttpStatus.FOUND, resp.getStatusCode());
        assertEquals(target, resp.getHeaders().getLocation());
        verify(shortenedAddressService).getForwardedURI(alias);
    }

    @Test
    void test_forwardToURL_returnsNotFoundWhenAliasMissing() {
        String alias = "missing";

        when(shortenedAddressService.getForwardedURI(alias)).thenReturn(Optional.empty());

        ResponseEntity<Object> resp = controller.forwardToURL(alias);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        verify(shortenedAddressService).getForwardedURI(alias);
    }

    @Test
    void test_deleteAlias_deletesStoredAliasWhenCalled() {
        String alias = "toDelete";

        doNothing().when(shortenedAddressService).deleteStoredAlias(alias);

        controller.deleteAlias(alias);

        verify(shortenedAddressService).deleteStoredAlias(alias);
    }

    @Test
    void test_getStoredAliases_returnsListWhenServiceHasStoredUrls() {
        List<StoredAlias> list = List.of(new StoredAlias("a", URI.create("http://x"), URI.create("http://s/a")));

        when(shortenedAddressService.getStoredURLs()).thenReturn(list);

        List<StoredAlias> result = controller.getStoredAliases();

        assertEquals(list, result);
        verify(shortenedAddressService).getStoredURLs();
    }
}
