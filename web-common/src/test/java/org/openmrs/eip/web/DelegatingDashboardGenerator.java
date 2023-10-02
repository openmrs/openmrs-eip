package org.openmrs.eip.web;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.web.controller.DashboardGenerator;

public class DelegatingDashboardGenerator extends BaseDashboardGenerator {
	
	private DashboardGenerator delegate;
	
	public DelegatingDashboardGenerator(ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
	@Override
	public Dashboard generate() {
		return delegate.generate();
	}
	
	@Override
	public String getCategorizationProperty() {
		return delegate.getCategorizationProperty();
	}
	
	@Override
	public List<String> getCategories(String entityName) {
		return delegate.getCategories(entityName);
	}
	
}
