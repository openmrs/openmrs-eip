package org.openmrs.eip.app.receiver;

import java.util.List;

import lombok.Getter;

public class MockReceiverSyncPrioritizingTask extends BaseReceiverSyncPrioritizingTask {
	
	@Getter
	boolean doRunCalled = false;
	
	public MockReceiverSyncPrioritizingTask() {
		super(null);
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
	
	@Override
	public List getNextBatch() {
		return null;
	}
	
}
