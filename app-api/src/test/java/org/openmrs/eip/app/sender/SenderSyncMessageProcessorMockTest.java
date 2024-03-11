package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.powermock.reflect.Whitebox;

public class SenderSyncMessageProcessorMockTest {
	
	private SenderSyncMessageProcessor processor;
	
	@Before
	public void setup() {
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SenderSyncMessageProcessor(null, null, null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("sync msg", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String table = "visit";
		final String msgUuid = "msg-uuid";
		final String uuid = "som-visit-uuid";
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setMessageUuid(msgUuid);
		msg.setTableName(table);
		msg.setIdentifier(uuid);
		assertEquals(table + "-" + uuid + "-" + msgUuid, processor.getThreadName(msg));
	}
	
	@Test
	public void getUniqueId_shouldReturnTheUuid() {
		final String visitUuid = "som-visit-uuid";
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setIdentifier(visitUuid);
		assertEquals(visitUuid, processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnTheTableName() {
		final String table = "visit";
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setTableName(table);
		assertEquals(table, processor.getLogicalType(msg));
	}
	
	@Test
	public void getLogicalTypeHierarchy_shouldReturnTheTablesInTheSameHierarchy() {
		assertEquals(1, processor.getLogicalTypeHierarchy("visit").size());
		assertEquals(2, processor.getLogicalTypeHierarchy("person").size());
		assertEquals(3, processor.getLogicalTypeHierarchy("orders").size());
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("sync-msg", processor.getQueueName());
	}
	
}
