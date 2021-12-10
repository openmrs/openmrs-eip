package org.openmrs.eip.app.management.entity;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.BaseSyncRequest;
import org.openmrs.eip.app.management.entity.BaseSyncRequest.Resolution;
import org.openmrs.eip.app.management.entity.SenderSyncRequest;

public class BaseSyncRequestTest {
	
	@Test
	public void updateResolution_shouldUpdateTheResolutionAndSetDateChanged() {
		BaseSyncRequest request = new SenderSyncRequest();
		Assert.assertNull(request.getResolution());
		Assert.assertNull(request.getDateChanged());
		
		request.updateResolution(Resolution.FOUND);
		
		Assert.assertEquals(Resolution.FOUND, request.getResolution());
		Assert.assertNotNull(request.getDateChanged());
	}
	
}
