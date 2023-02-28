package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;

public class MockPostSyncActionRunnable extends BasePostSyncActionRunnable {
	
	public MockPostSyncActionRunnable(SiteInfo siteInfo) {
		super(siteInfo, PostSyncActionType.SEND_RESPONSE, 10);
	}
	
	@Override
	public void process(List<PostSyncAction> actions) throws Exception {
	}
	
	@Override
	public String getProcessorName() {
		return "test processor";
	}
	
}
