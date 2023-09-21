package org.openmrs.eip.web;

import org.openmrs.eip.web.contoller.DashboardGenerator;

public class MockDashboardGenerator implements DashboardGenerator {
	
	private DashboardGenerator delegate;
	
	@Override
	public Dashboard generate() {
		return delegate.generate();
	}
	
	@Override
	public DashboardMetadata createMetadata() {
		return delegate.createMetadata();
	}
	
}
