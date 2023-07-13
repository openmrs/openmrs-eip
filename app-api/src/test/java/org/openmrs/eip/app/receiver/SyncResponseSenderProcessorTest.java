package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReceiverUtils.class)
public class SyncResponseSenderProcessorTest {
	
	private SyncResponseSenderProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SyncResponseSenderProcessor(null);
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
		assertEquals(siteUuid + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-" + uuid + "-" + messageUuid,
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
		final Long id = 2L;
		SyncedMessage msg = new SyncedMessage();
		msg.setId(id);
		assertFalse(msg.isResponseSent());
		processor = new SyncResponseSenderProcessor(null);
		
		processor.processItem(msg);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updateColumn("receiver_synced_msg", "response_sent", id, true);
	}
}
