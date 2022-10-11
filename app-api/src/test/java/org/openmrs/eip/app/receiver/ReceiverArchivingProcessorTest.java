package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.model.PersonModel;

public class ReceiverArchivingProcessorTest {
	
	private ReceiverArchivingProcessor processor = new ReceiverArchivingProcessor();
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("receiver archive", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String uuid = "person-uuid";
		final String msgUuid = "message-uuid";
		final Long id = 2L;
		SyncMessage msg = new SyncMessage();
		msg.setModelClassName(PersonModel.class.getName());
		msg.setId(id);
		msg.setMessageUuid(msgUuid);
		assertEquals(PersonModel.class.getName() + "-" + uuid + "-" + msgUuid + "-" + id, processor.getThreadName(msg));
	}
	
	@Test
	public void getItemKey_shouldReturnTheKeyContainingTableAndId() {
		final Long id = 2L;
		SyncMessage msg = new SyncMessage();
		msg.setId(id);
		assertEquals(id.toString(), processor.getItemKey(msg));
	}
	
	@Test
	public void processInParallel_shouldAlwaysReturnTrue() {
		Assert.assertTrue(processor.processInParallel(null));
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("receiver-archive", processor.getQueueName());
	}
	
	@Test
	public void getDestinationUri_shouldReturnTheUriToSendToEventsForProcessing() {
		assertEquals("", processor.getDestinationUri());
	}
	
}
