package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.reflect.Whitebox;

public class SyncResponseSenderProcessorTest {
	
	private SyncResponseSenderProcessor processor;
	
	@Before
	public void setup() {
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SyncResponseSenderProcessor(null, null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("response sender", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String uuid = "uuid";
		final String messageUuid = "message-uuid";
		final String siteUuid = "site-uuid";
		SyncedMessage msg = new SyncedMessage();
		msg.setModelClassName(PersonModel.class.getName());
		msg.setIdentifier(uuid);
		msg.setMessageUuid(messageUuid);
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setIdentifier(siteUuid);
		msg.setSite(siteInfo);
		assertEquals(siteUuid + "-" + messageUuid + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-" + uuid,
		    processor.getThreadName(msg));
	}
	
	@Test
	public void getUniqueId_shouldReturnDatabaseId() {
		final Long id = 7L;
		SyncedMessage msg = new SyncedMessage();
		msg.setId(id);
		assertEquals(id.toString(), processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheModelClassName() {
		assertEquals(SyncedMessage.class.getName(), processor.getLogicalType(new SyncedMessage()));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheLogicalTypeHierarchy() {
		assertNull(processor.getLogicalTypeHierarchy(PersonModel.class.getName()));
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("response-sender", processor.getQueueName());
	}
	
	@Test
	public void processItem_shouldMarkTheMessageThatTheResponseIsSent() {
		SyncedMessage msg = new SyncedMessage();
		assertFalse(msg.isResponseSent());
		SyncedMessageRepository mockRepo = Mockito.mock(SyncedMessageRepository.class);
		processor = new SyncResponseSenderProcessor(null, mockRepo);
		
		processor.processItem(msg);
		
		assertTrue(msg.isResponseSent());
		Mockito.verify(mockRepo).save(msg);
	}
}
