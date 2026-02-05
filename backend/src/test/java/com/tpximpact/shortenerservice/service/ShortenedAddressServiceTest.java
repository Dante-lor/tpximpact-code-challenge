package com.tpximpact.shortenerservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpximpact.shortenerservice.exception.NoSuchAliasException;
import com.tpximpact.shortenerservice.exception.ValidationFailedException;
import com.tpximpact.shortenerservice.model.ShortenedAddress;
import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ShortenResponse;
import com.tpximpact.shortenerservice.model.StoredAlias;
import com.tpximpact.shortenerservice.model.ValidationResult;
import com.tpximpact.shortenerservice.repository.ShortenedAddressDAO;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ShortenedAddressServiceTest {

    @Mock
    private ShortenedAddressDAO dao;

    @Mock
    private ShortenRequestValidationService validationService;

    @Mock
    private HttpServletRequest httpServletRequest;

    private ShortenedAddressService service;

    private final int maxAliasSize = 8;

    @BeforeEach
    void setup() {
        service = new ShortenedAddressService(dao, validationService, httpServletRequest, maxAliasSize);
    }

    @Test
    void test_shorten_withCustomAliasSavesAndReturnsShortUrl() {
        ShortenRequest req = new ShortenRequest(URI.create("http://example.com/page"), "my-alias");
        when(validationService.validate(req)).thenReturn(new ValidationResult());

        ShortenedAddress saved = ShortenedAddress.builder()
                .id(1L)
                .alias("my-alias")
                .originalUrl(req.fullUrl().toString())
                .build();

        when(dao.save(any(ShortenedAddress.class))).thenReturn(saved);

        ShortenResponse resp = service.shorten(req);

        assertEquals(URI.create("http://localhost:8080/my-alias"), resp.shortUrl());
    }

    @Test
    void test_shorten_whenValidationFailsThrows() {
        ShortenRequest req = new ShortenRequest(URI.create("http://ex"), null);
        when(validationService.validate(req)).thenReturn(new ValidationResult(List.of("bad")));

        assertThrows(ValidationFailedException.class, () -> service.shorten(req));
    }

    @Test
    void test_shorten_withoutCustomAliasGeneratesAliasAndSaves() {
        ShortenRequest req = new ShortenRequest(URI.create("http://example.com/long"), null);
        when(validationService.validate(req)).thenReturn(new ValidationResult());

        ShortenedAddress returned = ShortenedAddress.builder()
                .id(2L)
                .alias("generated")
                .originalUrl(req.fullUrl().toString())
                .build();

        when(dao.save(any(ShortenedAddress.class))).thenReturn(returned);

        ShortenResponse resp = service.shorten(req);

        assertEquals(URI.create("http://localhost:8080/generated"), resp.shortUrl());

        ArgumentCaptor<ShortenedAddress> captor = ArgumentCaptor.forClass(ShortenedAddress.class);
        verify(dao).save(captor.capture());
        ShortenedAddress savedArg = captor.getValue();
        assertNotNull(savedArg.getAlias());
        assertFalse(savedArg.getAlias().isBlank());
        assertEquals(req.fullUrl().toString(), savedArg.getOriginalUrl());
    }

    @Test
    void test_getForwardedURI_whenFoundReturnsUri() {
        String alias = "x1";
        when(dao.findByAlias(alias)).thenReturn(Optional.of(
            ShortenedAddress.builder().id(3L).alias(alias).originalUrl("http://upstream").build()
        ));

        Optional<URI> uri = service.getForwardedURI(alias);

        assertEquals(Optional.of(URI.create("http://upstream")), uri);
    }

    @Test
    void test_getForwardedURI_whenNotFoundReturnsEmpty() {
        when(dao.findByAlias("nope")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), service.getForwardedURI("nope"));
    }

    @Test
    void test_deleteStoredAlias_whenExistsDeletesById() {
        String alias = "del-me";
        ShortenedAddress addr = ShortenedAddress.builder().id(5L).alias(alias).originalUrl("http://x").build();
        when(dao.findByAlias(alias)).thenReturn(Optional.of(addr));

        service.deleteStoredAlias(alias);

        verify(dao).deleteById(5L);
    }

    @Test
    void test_deleteStoredAlias_whenMissingThrows() {
        when(dao.findByAlias("missing")).thenReturn(Optional.empty());

        assertThrows(NoSuchAliasException.class, () -> service.deleteStoredAlias("missing"));
    }

    @Test
    void test_getStoredURLsMapsAllEntries() {
        ShortenedAddress a = ShortenedAddress.builder().id(7L).alias("a1").originalUrl("http://one").build();
        ShortenedAddress b = ShortenedAddress.builder().id(8L).alias("b2").originalUrl("http://two").build();

        when(dao.findAll()).thenReturn(List.of(a, b));

        List<StoredAlias> list = service.getStoredURLs();

        assertEquals(2, list.size());
        assertEquals("a1", list.get(0).alias());
        assertEquals(URI.create("http://one"), list.get(0).fullUrl());
        assertEquals(URI.create("http://localhost:8080/a1"), list.get(0).shortUrl());
    }

}
