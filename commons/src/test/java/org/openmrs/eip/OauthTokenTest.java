package org.openmrs.eip;

import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneId.systemDefault;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class OauthTokenTest {
	
	@Test
	public void isExpired_shouldReturnTrueIfAsOfDatetimeIsAfterExpiryDatetime() {
		final long expiresAt = 1626898515;
		LocalDateTime asOfDate = ofEpochSecond(expiresAt + 1).atZone(systemDefault()).toLocalDateTime();
		assertTrue(new OauthToken(null, expiresAt).isExpired(asOfDate));
	}
	
	@Test
	public void isExpired_shouldReturnTrueIfAsOfDatetimeIsEqualToExpiryDatetime() {
		final long expiresAt = 1626898515;
		LocalDateTime asOfDate = ofEpochSecond(expiresAt).atZone(systemDefault()).toLocalDateTime();
		assertTrue(new OauthToken(null, expiresAt).isExpired(asOfDate));
	}
	
	@Test
	public void isExpired_shouldReturnFalseIfAsOfDatetimeIsBeforeExpiryDatetime() {
		final long expiresAt = 1626898515;
		LocalDateTime asOfDate = ofEpochSecond(expiresAt - 1).atZone(systemDefault()).toLocalDateTime();
		assertFalse(new OauthToken(null, expiresAt).isExpired(asOfDate));
	}
	
}
