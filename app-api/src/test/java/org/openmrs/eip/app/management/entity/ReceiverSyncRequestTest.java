package org.openmrs.eip.app.management.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReceiverSyncRequest.ReceiverRequestStatus;

public class ReceiverSyncRequestTest {
	
	@Test
	public void markAsSent_shouldSetStatusToSentAndSetDateSent() {
		ReceiverSyncRequest request = new ReceiverSyncRequest();
		Assert.assertNull(request.getStatus());
		Assert.assertNull(request.getDateSent());
		
		request.markAsSent();
		
		assertEquals(ReceiverRequestStatus.SENT, request.getStatus());
		Assert.assertNotNull(request.getDateSent());
	}
	
	@Test
	public void markAsReceived_shouldSetStatusToReceivedAndSetDateReceived() {
		ReceiverSyncRequest request = new ReceiverSyncRequest();
		Assert.assertNull(request.getStatus());
		Assert.assertNull(request.getDateReceived());
		
		request.markAsReceived();
		
		assertEquals(ReceiverRequestStatus.RECEIVED, request.getStatus());
		Assert.assertNotNull(request.getDateReceived());
	}
	
	@Test
	public void buildRequest_shouldCreateASyncRequestObjectFromTheReceiverRequest() {
		final String table = "person";
		final String identifier = "12345";
		final String requestUuid = "f7f5b2d2abc1952a22274e269c47e992";
		ReceiverSyncRequest request = new ReceiverSyncRequest();
		request.setTableName(table);
		request.setIdentifier(identifier);
		request.setRequestUuid(requestUuid);
		
		SyncRequestModel model = request.buildRequest();
		
		assertEquals(table, model.getTableName());
		assertEquals(identifier, model.getIdentifier());
		assertEquals(requestUuid, model.getRequestUuid());
	}
	
}
