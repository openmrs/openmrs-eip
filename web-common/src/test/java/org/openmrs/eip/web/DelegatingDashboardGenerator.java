package org.openmrs.eip.web;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.SyncOperation;
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
	public String getCategorizationProperty(String entityType) {
		return delegate.getCategorizationProperty(entityType);
	}
	
	@Override
	public List<String> getCategories(String entityName) {
		return delegate.getCategories(entityName);
	}
	
	@Override
	public Integer getCount(String entityType, String category, SyncOperation op) {
		return delegate.getCount(entityType, category, op);
	}
	
}
