package org.openmrs.eip.web.sender;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;

public class SenderDashboardGeneratorTest {
	
	private SenderDashboardGenerator generator = new SenderDashboardGenerator(null, null);
	
	@Test
	public void getCategorizationProperty_shouldReturnTableName() {
		Assert.assertEquals("tableName", generator.getCategorizationProperty(SenderSyncMessage.class.getSimpleName()));
	}
	
	@Test
	public void getCategorizationProperty_shouldReturnEventTableNameForDebeziumEvent() {
		Assert.assertEquals("event.tableName", generator.getCategorizationProperty(DebeziumEvent.class.getSimpleName()));
	}
	
}
