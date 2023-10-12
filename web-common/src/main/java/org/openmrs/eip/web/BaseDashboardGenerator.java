package org.openmrs.eip.web;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.web.controller.DashboardGenerator;

public abstract class BaseDashboardGenerator implements DashboardGenerator {
	
	private ProducerTemplate producerTemplate;
	
	public BaseDashboardGenerator(ProducerTemplate producerTemplate) {
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
		
		return producerTemplate.requestBody(
		    "jpa:" + entityType + "?query=SELECT count(*) FROM " + entityType + " WHERE "
		            + getCategorizationProperty(entityType) + " = '" + category + "' AND operation = '" + op + "'",
		    null, Integer.class);
	}
	
}
