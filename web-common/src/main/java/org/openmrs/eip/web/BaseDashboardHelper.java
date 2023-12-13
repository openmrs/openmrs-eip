package org.openmrs.eip.web;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.web.controller.DashboardHelper;

public abstract class BaseDashboardHelper implements DashboardHelper {
	
	private ProducerTemplate producerTemplate;
	
	public BaseDashboardHelper(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}
	
	@Override
	public List<String> getCategories(String entityType) {
		return producerTemplate.requestBody(
		    "jpa:" + entityType + "?query=SELECT DISTINCT " + getCategorizationProperty(entityType) + " FROM " + entityType,
		    null, List.class);
	}
	
	@Override
	public Integer getCount(String entityType, String category, SyncOperation op) {
		if (category == null && op == null) {
			return producerTemplate.requestBody("jpa:" + entityType + "?query=SELECT count(*) FROM " + entityType, null,
			    Integer.class);
		}
		
		String opProp = "operation";
		if (DebeziumEvent.class.getSimpleName().equals(entityType)
		        || SenderRetryQueueItem.class.getSimpleName().equals(entityType)) {
			opProp = "event." + opProp;
		}
		
		final String q = "jpa:" + entityType + "?query=SELECT count(*) FROM " + entityType + " WHERE "
		        + getCategorizationProperty(entityType) + " = '" + category + "' AND " + opProp + " = '" + op + "'";
		return producerTemplate.requestBody(q, null, Integer.class);
	}
	
}
