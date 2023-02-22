package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.model.PersonModel;

public class SyncedMessageItemizingProcessorTest {
	
	private SyncedMessageItemizingProcessor processor = new SyncedMessageItemizingProcessor();
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("synced msg", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String uuid = "uuid";
		final String messageUuid = "message-uuid";
		final Long id = 2L;
		final String siteUuid = "site-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setId(id);
		msg.setIdentifier(uuid);
		msg.setMessageUuid(messageUuid);
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(siteUuid);
		msg.setSite(siteInfo);
		assertEquals(
		    siteUuid + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-" + uuid + "-" + messageUuid + "-" + id,
		    processor.getThreadName(msg));
	}
	
	@Test
	public void getUniqueId_shouldReturnDatabaseId() {
		final Long id = 2L;
		SyncedMessage msg = new SyncedMessage();
		msg.setId(id);
		assertEquals(id.toString(), processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheTableName() {
		final String type = PersonModel.class.getName();
		SyncedMessage msg = new SyncedMessage();
		msg.setModelClassName(type);
		assertEquals(type, processor.getLogicalType(msg));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchy() {
		assertEquals(2, processor.getLogicalTypeHierarchy(PersonModel.class.getName()).size());
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("synced-msg", processor.getQueueName());
	}
	
	@Test
	public void getDestinationUri_shouldReturnTheUriToSendToEventsForProcessing() {
		assertEquals(ReceiverConstants.URI_RECEIVER_UTILS, processor.getDestinationUri());
	}
	
}
