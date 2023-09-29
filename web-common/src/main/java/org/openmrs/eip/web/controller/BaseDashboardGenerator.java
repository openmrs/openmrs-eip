package org.openmrs.eip.web.controller;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.SyncOperation;

public abstract class BaseDashboardGenerator implements DashboardGenerator {
	
	private ProducerTemplate producerTemplate;
	
	public BaseDashboardGenerator(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}
	
	@Override
	public List<String> getCategories(String entityName) {
		return producerTemplate.requestBody(
		    "jpa:" + entityName + "?query=SELECT DISTINCT " + getCategorizationProperty() + " FROM " + entityName, null,
		    List.class);
	}
	
	protected Integer getTotalCount(String entityName) {
		return producerTemplate.requestBody("jpa:" + entityName + "?query=SELECT count(*) FROM " + entityName, null,
		    Integer.class);
	}
	
	protected Integer getCount(String entityName, String groupingPropertyValue, SyncOperation op) {
		return producerTemplate.requestBody("jpa:" + entityName + "?query=SELECT count(*) FROM " + entityName + " WHERE "
		        + getCategorizationProperty() + " = '" + groupingPropertyValue + "' AND operation = '" + op + "'",
		    null, Integer.class);
	}
	
}
