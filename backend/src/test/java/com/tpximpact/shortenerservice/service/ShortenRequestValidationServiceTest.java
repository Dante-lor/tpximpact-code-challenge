package com.tpximpact.shortenerservice.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpximpact.shortenerservice.model.ShortenRequest;
import com.tpximpact.shortenerservice.model.ShortenedAddress;
import com.tpximpact.shortenerservice.model.ValidationResult;
import com.tpximpact.shortenerservice.repository.ShortenedAddressDAO;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortenRequestValidationServiceTest {

	@Mock
	private ShortenedAddressDAO dao;

	private ShortenRequestValidationService validationService;

	private final int maxAliasSize = 5;

	@BeforeEach
	void setup() {
		validationService = new ShortenRequestValidationService(maxAliasSize, dao);
	}

	@Test
	void test_validate_returnsErrorWhenRequestIsNull() {
		ValidationResult result = validationService.validate(null);
		assertFalse(result.isValid());
		assertTrue(result.errors().contains("request cannot be null"));
	}

	@Test
	void test_validate_returnsErrorWhenFullUrlMissing() {
		ShortenRequest req = new ShortenRequest(null, null);
		ValidationResult result = validationService.validate(req);
		assertFalse(result.isValid());
		assertTrue(result.errors().contains("full url must be provided"));
	}

	@Test
	void test_validate_returnsErrorWhenAliasHasInvalidCharacters() {
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), "Bad!");
		ValidationResult result = validationService.validate(req);
		assertFalse(result.isValid());
		assertTrue(result.errors().contains("alias must only contain lowercase letters, numbers and dashes"));
	}

	@Test
	void test_validate_returnsErrorWhenAliasTooLong() {
		// alias length 6 when maxAliasSize is 5
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), "abcdef");
		ValidationResult result = validationService.validate(req);
		assertFalse(result.isValid());
		assertTrue(result.errors().stream().anyMatch(s -> s.contains("the max size for any alias is ")));
		assertTrue(result.errors().contains("the max size for any alias is " + maxAliasSize + " characters"));
	}

	@Test
	void test_validate_returnsErrorWhenAliasBlank() {
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), "   ");
		ValidationResult result = validationService.validate(req);
		assertFalse(result.isValid());
		// blank alias also violates allowed characters
		assertTrue(result.errors().contains("aliases cannot be blank"));
	}

	@Test
	void test_validate_returnsErrorWhenAliasAlreadyExists() {
		String alias = "taken";
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), alias);
		when(dao.findByAlias(alias)).thenReturn(Optional.of(
			ShortenedAddress.builder().alias(alias).originalUrl("http://original").build()
		));

		ValidationResult result = validationService.validate(req);
		assertFalse(result.isValid());
		assertTrue(result.errors().contains("the alias " + alias + " is already mapped to a URL"));
	}

	@Test
	void test_validate_returnsErrorWhenCustomAliasIsBlank() {
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), "");
		ValidationResult result = validationService.validate(req);
		assertFalse(result.isValid());
		assertTrue(result.errors().contains("aliases cannot be blank"));
	}

	@ParameterizedTest
	@ValueSource(strings = { "error", "urls"})
	void test_validate_returnsErrorWhenUsingReservedPath(String alias) {
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), alias);

		ValidationResult result = validationService.validate(req);
		assertFalse(result.isValid());
		assertTrue(result.errors().contains("The alias " + alias + " is not permitted as it clashes with other paths"));
	}
	
	@Test
	void test_validate_isValidWhenNoCustomAliasProvided() {
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), null);
		ValidationResult result = validationService.validate(req);
		assertTrue(result.isValid());
		assertEquals(0, result.errors().size());
	}

	@Test
	void test_validate_isValidwhenCustomAliasValidAndUnique() {
		String alias = "o_K-4";
		ShortenRequest req = new ShortenRequest(URI.create("http://example.com"), alias);
		when(dao.findByAlias(alias)).thenReturn(Optional.empty());

		ValidationResult result = validationService.validate(req);
		assertTrue(result.isValid());
		assertEquals(0, result.errors().size());
	}

}
