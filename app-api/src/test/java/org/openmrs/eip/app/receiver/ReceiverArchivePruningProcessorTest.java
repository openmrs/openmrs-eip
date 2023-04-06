package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.service.ReceiverArchiveService;
import org.powermock.reflect.Whitebox;

public class ReceiverArchivePruningProcessorTest {
	
	private ReceiverArchivePruningProcessor processor;
	
	@Mock
	private ReceiverArchiveService mockService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ReceiverArchivePruningProcessor(null, mockService);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getThreadName_shouldReturnArchiveMessageUuid() {
		final String messageUuid = "message-uuid";
		ReceiverSyncArchive archive = new ReceiverSyncArchive();
		archive.setMessageUuid(messageUuid);
		assertEquals(messageUuid, processor.getThreadName(archive));
	}
	
	@Test
	public void processItem_shouldCallTheServiceToPruneTheArchive() {
		ReceiverSyncArchive archive = new ReceiverSyncArchive();
		
		processor.processItem(archive);
		
		Mockito.verify(mockService).prune(archive);
	}
	
}
