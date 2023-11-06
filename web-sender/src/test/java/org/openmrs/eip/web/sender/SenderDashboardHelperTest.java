package org.openmrs.eip.web.sender;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;

public class SenderDashboardHelperTest {
	
	private SenderDashboardHelper helper = new SenderDashboardHelper(null, null);
	
	@Test
	public void getCategorizationProperty_shouldReturnTableName() {
		assertEquals("tableName", helper.getCategorizationProperty(SenderSyncMessage.class.getSimpleName()));
	}
	
	@Test
	public void getCategorizationProperty_shouldReturnEventTableNameForDebeziumEvent() {
		assertEquals("event.tableName", helper.getCategorizationProperty(DebeziumEvent.class.getSimpleName()));
	}
	
	@Test
	public void getCategorizationProperty_shouldReturnEventTableNameForARetry() {
		assertEquals("event.tableName", helper.getCategorizationProperty(SenderRetryQueueItem.class.getSimpleName()));
	}
	
}
