package org.openmrs.eip.app.management.entity.sender;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderSyncRequest;
import org.openmrs.eip.app.management.entity.sender.SenderSyncRequest.SenderRequestStatus;

public class SenderSyncRequestTest {
	
	@Test
	public void markAsProcessed_shouldSetStatusToProcessedAndSetDateSentAndSetFoundToTrue() {
		SenderSyncRequest request = new SenderSyncRequest();
		Assert.assertEquals(SenderRequestStatus.NEW, request.getStatus());
		Assert.assertNull(request.getDateProcessed());
		Assert.assertFalse(request.getFound());
		
		request.markAsProcessed(true);
		
		assertEquals(SenderRequestStatus.PROCESSED, request.getStatus());
		Assert.assertNotNull(request.getDateProcessed());
		Assert.assertTrue(request.getFound());
	}
	
	@Test
	public void markAsProcessed_shouldSetStatusToProcessedAndSetDateSentAndSetFoundToFalse() {
		SenderSyncRequest request = new SenderSyncRequest();
		Assert.assertEquals(SenderRequestStatus.NEW, request.getStatus());
		Assert.assertNull(request.getDateProcessed());
		Assert.assertFalse(request.getFound());
		
		request.markAsProcessed(false);
		
		assertEquals(SenderRequestStatus.PROCESSED, request.getStatus());
		Assert.assertNotNull(request.getDateProcessed());
		Assert.assertFalse(request.getFound());
	}
	
}
