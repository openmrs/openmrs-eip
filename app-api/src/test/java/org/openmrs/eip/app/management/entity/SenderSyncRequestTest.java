package org.openmrs.eip.app.management.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncRequest.SenderRequestStatus;

public class SenderSyncRequestTest {
	
	@Test
	public void markAsSent_shouldSetStatusToSentAndSetDateSent() {
		SenderSyncRequest request = new SenderSyncRequest();
		Assert.assertNull(request.getStatus());
		Assert.assertNull(request.getDateSent());
		
		request.markAsSent();
		
		assertEquals(SenderRequestStatus.SENT, request.getStatus());
		Assert.assertNotNull(request.getDateSent());
	}
	
}
