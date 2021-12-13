package org.openmrs.eip.app.management.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncRequest.SenderRequestStatus;

public class SenderSyncRequestTest {
	
	@Test
	public void markAsSent_shouldSetStatusToSentAndSetDateSentAndSetFoundToTrue() {
		SenderSyncRequest request = new SenderSyncRequest();
		Assert.assertNull(request.getStatus());
		Assert.assertNull(request.getDateSent());
		Assert.assertFalse(request.getFound());
		
		request.markAsSent(true);
		
		assertEquals(SenderRequestStatus.SENT, request.getStatus());
		Assert.assertNotNull(request.getDateSent());
		Assert.assertTrue(request.getFound());
	}
	
	@Test
	public void markAsSent_shouldSetStatusToSentAndSetDateSentAndSetFoundToFalse() {
		SenderSyncRequest request = new SenderSyncRequest();
		Assert.assertNull(request.getStatus());
		Assert.assertNull(request.getDateSent());
		Assert.assertFalse(request.getFound());
		
		request.markAsSent(false);
		
		assertEquals(SenderRequestStatus.SENT, request.getStatus());
		Assert.assertNotNull(request.getDateSent());
		Assert.assertFalse(request.getFound());
	}
	
}
