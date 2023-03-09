package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.SenderSyncResponse;
import org.powermock.reflect.Whitebox;

public class SenderSyncResponseProcessorTest {
	
	private SenderSyncResponseProcessor processor;
	
	@Before
	public void setup() {
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SenderSyncResponseProcessor(null, null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("sync response", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String messageUuid = "message-uuid";
		SenderSyncResponse msg = new SenderSyncResponse();
		msg.setMessageUuid(messageUuid);
		assertEquals(messageUuid, processor.getThreadName(msg));
	}
	
	@Test
	public void getUniqueId_shouldReturnDatabaseId() {
		final Long id = 2L;
		SenderSyncResponse msg = new SenderSyncResponse();
		msg.setId(id);
		assertEquals(id.toString(), processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheTableName() {
		assertEquals(SenderSyncResponse.class.getName(), processor.getLogicalType(new SenderSyncResponse()));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnNull() {
		Assert.assertNull(processor.getLogicalTypeHierarchy("visit"));
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("sync-response", processor.getQueueName());
	}
	
}
