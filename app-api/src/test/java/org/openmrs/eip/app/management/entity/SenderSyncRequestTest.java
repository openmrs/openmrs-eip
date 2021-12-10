package org.openmrs.eip.app.management.entity;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncRequest;
import org.openmrs.eip.app.management.entity.SenderSyncRequest.SenderRequestStatus;

public class SenderSyncRequestTest {
	
	@Test
	public void updateStatus_shouldUpdateTheStatusAndSetDateChanged() {
		SenderSyncRequest request = new SenderSyncRequest();
		Assert.assertNull(request.getStatus());
		Assert.assertNull(request.getDateChanged());
		
		request.updateStatus(SenderRequestStatus.PROCESSED);
		
		Assert.assertEquals(SenderRequestStatus.PROCESSED, request.getStatus());
		Assert.assertNotNull(request.getDateChanged());
	}
	
}
