package org.openmrs.eip.web;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;

public class TestDashboardHelper extends BaseDashboardHelper {
	
	private boolean isReceiver;
	
	public TestDashboardHelper(ProducerTemplate producerTemplate, boolean isReceiver) {
		super(producerTemplate);
		this.isReceiver = isReceiver;
	}
	
	@Override
	public String getCategorizationProperty(String entityType) {
		if (isReceiver) {
			return "modelClassName";
		}
		
		if (DebeziumEvent.class.getSimpleName().equals(entityType)
		        || SenderRetryQueueItem.class.getSimpleName().equals(entityType)) {
			return "event.tableName";
		}
		
		return "tableName";
	}
	
}
