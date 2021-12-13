package org.openmrs.eip.app.management.entity;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.BaseSyncRequest.Resolution;

public class BaseSyncRequestTest {
	
	@Test
	public void markAsFound_shouldSetResolutionToFoundAndResolutionDate() {
		BaseSyncRequest request = new SenderSyncRequest();
		Assert.assertNull(request.getResolution());
		Assert.assertNull(request.getResolutionDate());
		
		request.markAsFound();
		
		Assert.assertEquals(Resolution.FOUND, request.getResolution());
		Assert.assertNotNull(request.getResolutionDate());
	}
	
	@Test
	public void markAsNotFound_shouldSetResolutionToNotFoundAndResolutionDate() {
		BaseSyncRequest request = new SenderSyncRequest();
		Assert.assertNull(request.getResolution());
		Assert.assertNull(request.getResolutionDate());
		
		request.markAsNotFound();
		
		Assert.assertEquals(Resolution.NOT_FOUND, request.getResolution());
		Assert.assertNotNull(request.getResolutionDate());
	}
	
}
