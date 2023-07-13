package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReceiverUtils.class)
public class SyncedMessageDeletingProcessorTest {
	
	private SyncedMessageDeletingProcessor processor;
	
	@Mock
	private SyncedMessageRepository mockRepo;
	
	@Before
	public void setup() {
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SyncedMessageDeletingProcessor(null, mockRepo);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
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
	public void processItem_shouldProcessTheSpecifiedItem() {
		SyncedMessage msg = new SyncedMessage();
		
		processor.processItem(msg);
		
		Mockito.verify(mockRepo).delete(msg);
	}
	
}
