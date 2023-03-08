package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;

public class MockPostSyncActionRunnable extends BasePostSyncActionRunnable {
	
	public MockPostSyncActionRunnable(SiteInfo siteInfo) {
		super(siteInfo);
	}
	
	@Override
	public void process(List<SyncedMessage> actions) throws Exception {
	}
	
	@Override
	public String getTaskName() {
		return "test task";
	}
	
	@Override
	public List<SyncedMessage> getNextBatch() {
		return null;
	}
	
}
