package com.tpximpact.shortenerservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tpximpact.shortenerservice.model.ShortenedAddress;

@SpringBootTest
public class ShortenedAddressDAOTest {

    @Autowired
    private ShortenedAddressDAO addressDAO;

    @Test
    void test_save_shouldThrowExceptionIfDuplicateAliases() {
        // Given
        ShortenedAddress address = ShortenedAddress.builder().alias("same")
            .originalUrl("https://example.com")
            .build();

        // When
        addressDAO.save(address);

        ShortenedAddress sameAlias = ShortenedAddress.builder().alias("same")
            .originalUrl("https://different.com")
            .build();

        assertThrows(RuntimeException.class, () -> addressDAO.save(sameAlias));
    }

    @Test
    void test_save_shouldThrowExceptionIfOriginalURLIsNull() {
        // Given
        ShortenedAddress address = ShortenedAddress.builder().alias("same")
            .build();

        // When / Then
        assertThrows(RuntimeException.class, () -> addressDAO.save(address));
    }

    @Test
    void test_save_shouldThrowExceptionIfAliasIsNull() {
        // Given
        ShortenedAddress address = ShortenedAddress.builder().originalUrl("something")
            .build();

        // When / Then
        assertThrows(RuntimeException.class, () -> addressDAO.save(address));
    }

    @Test
    void test_findByAlias_shouldReturnEmptyOptionalWhenNoMatchIsFound() {
        assertTrue(addressDAO.findByAlias("not found").isEmpty());
    }

    @Test
    void test_findByAlias_shouldReturnPopulatedOptionalIfAMatchIsFound() {
        // Given
        ShortenedAddress address = ShortenedAddress.builder().alias("example")
            .originalUrl("https://example.com")
            .build();

        ShortenedAddress saved = addressDAO.save(address);

        // When
        Optional<ShortenedAddress> byAlias = addressDAO.findByAlias("example");

        // Then
        assertTrue(byAlias.isPresent());
        assertEquals(saved, byAlias.get());
    }
}
