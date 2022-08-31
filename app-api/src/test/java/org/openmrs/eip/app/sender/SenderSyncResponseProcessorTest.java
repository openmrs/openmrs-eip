package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncResponse;

public class SenderSyncResponseProcessorTest {
	
	private SenderSyncResponseProcessor processor = new SenderSyncResponseProcessor();
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("sync response", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String messageUuid = "message-uuid";
		final Long id = 2L;
		SenderSyncResponse msg = new SenderSyncResponse();
		msg.setId(id);
		msg.setMessageUuid(messageUuid);
		assertEquals(messageUuid + "-" + id, processor.getThreadName(msg));
	}
	
	@Test
	public void getItemKey_shouldReturnTheKeyContainingTableAndId() {
		final Long id = 2L;
		SenderSyncResponse msg = new SenderSyncResponse();
		msg.setId(id);
		assertEquals(id.toString(), processor.getItemKey(msg));
	}
	
	@Test
	public void processInParallel_shouldAlwaysReturnTrue() {
		Assert.assertTrue(processor.processInParallel(null));
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("sync-response", processor.getQueueName());
	}
	
	@Test
	public void getDestinationUri_shouldReturnTheUriToSendToEventsForProcessing() {
		assertEquals(SenderConstants.URI_RESPONSE_PROCESSOR, processor.getDestinationUri());
	}
	
}
