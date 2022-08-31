package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;

public class SenderSyncMessageProcessorTest {
	
	private SenderSyncMessageProcessor processor = new SenderSyncMessageProcessor();
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("sync msg", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String table = "visit";
		final Long id = 2L;
		final String uuid = "som-visit-uuid";
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setId(id);
		msg.setTableName(table);
		msg.setIdentifier(uuid);
		assertEquals(table + "-" + uuid + "-" + id, processor.getThreadName(msg));
	}
	
	@Test
	public void getItemKey_shouldReturnTheKeyContainingTableAndId() {
		final String table = "visit";
		final String uuid = "som-visit-uuid";
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setTableName(table);
		msg.setIdentifier(uuid);
		assertEquals(table + "#" + uuid, processor.getItemKey(msg));
	}
	
	@Test
	public void processInParallel_shouldReturnTrueForSnapshotEvent() {
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setSnapshot(true);
		Assert.assertTrue(processor.processInParallel(msg));
	}
	
	@Test
	public void processInParallel_shouldReturnFalseForNonSnapshotEvent() {
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setSnapshot(false);
		Assert.assertFalse(processor.processInParallel(msg));
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("sync-msg", processor.getQueueName());
	}
	
	@Test
	public void getDestinationUri_shouldReturnTheUriToSendToEventsForProcessing() {
		assertEquals(SenderConstants.URI_ACTIVEMQ_PUBLISHER, processor.getDestinationUri());
	}
	
}
