package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class MockPostSyncActionRunnable extends BasePostSyncActionRunnable {
	
	private Pageable page = PageRequest.of(0, 10);
	
	public MockPostSyncActionRunnable(SiteInfo siteInfo) {
		super(siteInfo);
	}
	
	@Override
	public PostSyncActionType getActionType() {
		return PostSyncActionType.SEND_RESPONSE;
	}
	
	@Override
	public List<PostSyncAction> process(List<PostSyncAction> actions) throws Exception {
		return null;
	}
	
	@Override
	public String getProcessorName() {
		return "test processor";
	}
	
	@Override
	public Pageable getPageable() {
		return page;
	}
	
}
