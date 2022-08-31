package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.entity.Event;

public class DebeziumEventProcessorTest {
	
	private DebeziumEventProcessor processor = new DebeziumEventProcessor();
	
	private DebeziumEvent createEvent() {
		DebeziumEvent de = new DebeziumEvent();
		de.setEvent(new Event());
		return de;
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("db event", processor.getProcessorName());
	}
	
	@Test
	public void getThreadName_shouldReturnTheThreadNameContainingEventDetails() {
		final String table = "visit";
		final String visitId = "4";
		final Long id = 2L;
		final String uuid = "som-visit-uuid";
		DebeziumEvent de = createEvent();
		de.setId(id);
		de.getEvent().setTableName(table);
		de.getEvent().setIdentifier(uuid);
		de.getEvent().setPrimaryKeyId(visitId);
		assertEquals(table + "-" + visitId + "-" + id + "-" + uuid, processor.getThreadName(de));
	}
	
	@Test
	public void getThreadName_shouldExcludeTheIdentifierInTheThreadNameIfNotSpecified() {
		final String table = "visit";
		final String visitId = "4";
		final Long id = 2L;
		DebeziumEvent de = createEvent();
		de.setId(id);
		de.getEvent().setTableName(table);
		de.getEvent().setPrimaryKeyId(visitId);
		assertEquals(table + "-" + visitId + "-" + id, processor.getThreadName(de));
	}
	
	@Test
	public void getItemKey_shouldReturnTheKeyContainingTableAndId() {
		final String table = "visit";
		final String visitId = "4";
		final String uuid = "som-visit-uuid";
		DebeziumEvent de = createEvent();
		de.getEvent().setTableName(table);
		de.getEvent().setIdentifier(uuid);
		de.getEvent().setPrimaryKeyId(visitId);
		assertEquals(table + "#" + visitId, processor.getItemKey(de));
	}
	
	@Test
	public void processInParallel_shouldReturnTrueForSnapshotEvent() {
		DebeziumEvent de = createEvent();
		de.getEvent().setSnapshot(true);
		Assert.assertTrue(processor.processInParallel(de));
	}
	
	@Test
	public void processInParallel_shouldReturnFalseForNonSnapshotEvent() {
		DebeziumEvent de = createEvent();
		de.getEvent().setSnapshot(false);
		Assert.assertFalse(processor.processInParallel(de));
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("db-event", processor.getQueueName());
	}
	
	@Test
	public void getDestinationUri_shouldReturnTheUriToSendToEventsForProcessing() {
		assertEquals(SenderConstants.URI_DBZM_EVENT_PROCESSOR, processor.getDestinationUri());
	}
	
}
