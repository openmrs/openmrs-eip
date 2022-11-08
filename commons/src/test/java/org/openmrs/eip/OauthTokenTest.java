package org.openmrs.eip;

import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneId.systemDefault;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

public class OauthTokenTest {
	
	@Test
	public void isExpired_shouldReturnTrueIfAsOfDatetimeIsAfterExpiryDatetime() {
		final long expiresAt = 1626898515;
		LocalDateTime asOfDate = ofEpochSecond(expiresAt + 1).atZone(systemDefault()).toLocalDateTime().minusSeconds(10);
		Assert.assertTrue(new OauthToken(null, expiresAt).isExpired(asOfDate));
	}
	
	@Test
	public void isExpired_shouldReturnTrueIfAsOfDatetimeIsEqualToExpiryDatetime() {
		final long expiresAt = 1626898515;
		LocalDateTime asOfDate = ofEpochSecond(expiresAt).atZone(systemDefault()).toLocalDateTime().minusSeconds(10);
		Assert.assertTrue(new OauthToken(null, expiresAt).isExpired(asOfDate));
	}
	
	@Test
	public void isExpired_shouldReturnFalseIfAsOfDatetimeIsBeforeExpiryDatetime() {
		final long expiresAt = 1626898515;
		LocalDateTime asOfDate = ofEpochSecond(expiresAt - 1).atZone(systemDefault()).toLocalDateTime().minusSeconds(10);
		Assert.assertFalse(new OauthToken(null, expiresAt).isExpired(asOfDate));
	}
	
}
