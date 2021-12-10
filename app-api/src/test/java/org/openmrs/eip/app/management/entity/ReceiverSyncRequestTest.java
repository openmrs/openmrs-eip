package org.openmrs.eip.app.management.entity;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest.ReceiverRequestStatus;

public class ReceiverSyncRequestTest {
	
	@Test
	public void updateStatus_shouldUpdateTheStatusAndSetDateChanged() {
		ReceiverSyncRequest request = new ReceiverSyncRequest();
		Assert.assertNull(request.getStatus());
		Assert.assertNull(request.getDateChanged());
		
		request.updateStatus(ReceiverRequestStatus.RECEIVED);
		
		Assert.assertEquals(ReceiverRequestStatus.RECEIVED, request.getStatus());
		Assert.assertNotNull(request.getDateChanged());
	}
	
}
