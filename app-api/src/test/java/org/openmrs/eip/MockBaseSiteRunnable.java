package org.openmrs.eip;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.receiver.BaseSiteRunnable;

import lombok.Getter;

public class MockBaseSiteRunnable extends BaseSiteRunnable {
	
	@Getter
	boolean doRunCalled = false;
	
	public MockBaseSiteRunnable(SiteInfo mockSite) {
		super(mockSite);
	}
	
	@Override
	public String getTaskName() {
		return null;
	}
	
	@Override
	public boolean doRun() {
		doRunCalled = true;
		return true;
	}
	
}
