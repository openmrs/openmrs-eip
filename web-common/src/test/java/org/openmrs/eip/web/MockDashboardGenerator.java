package org.openmrs.eip.web;

import java.util.List;

import org.openmrs.eip.web.controller.BaseDashboardGenerator;
import org.openmrs.eip.web.controller.DashboardGenerator;

public class MockDashboardGenerator extends BaseDashboardGenerator {
	
	private DashboardGenerator delegate;
	
	public MockDashboardGenerator() {
		super(null);
	}
	
	@Override
	public Dashboard generate() {
		return delegate.generate();
	}
	
	@Override
	public String getCategorizationProperty() {
		return null;
	}
	
	@Override
	public List<String> getCategories(String entityName) {
		return delegate.getCategories(entityName);
	}
	
}
