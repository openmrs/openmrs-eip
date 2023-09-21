package org.openmrs.eip.web;

import java.util.List;

import org.openmrs.eip.web.controller.DashboardGenerator;

public class MockDashboardGenerator implements DashboardGenerator {
	
	private DashboardGenerator delegate;
	
	@Override
	public Dashboard generate() {
		return delegate.generate();
	}
	
	@Override
	public List<String> getGroups() {
		return delegate.getGroups();
	}
	
}
