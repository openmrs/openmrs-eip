package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.service.SenderService;
import org.powermock.reflect.Whitebox;

public class SenderArchivePruningProcessorTest {
	
	private SenderArchivePruningProcessor processor;
	
	@Mock
	private SenderService mockService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SenderArchivePruningProcessor(null, mockService);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getThreadName_shouldReturnArchiveMessageUuid() {
		final String messageUuid = "message-uuid";
		SenderSyncArchive archive = new SenderSyncArchive();
		archive.setMessageUuid(messageUuid);
		assertEquals(messageUuid, processor.getThreadName(archive));
	}
	
	@Test
	public void processItem_shouldCallTheServiceToPruneTheArchive() {
		SenderSyncArchive archive = new SenderSyncArchive();
		
		processor.processItem(archive);
		
		Mockito.verify(mockService).prune(archive);
	}
	
}
