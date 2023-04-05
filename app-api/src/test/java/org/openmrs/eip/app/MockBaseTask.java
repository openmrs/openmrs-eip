package org.openmrs.eip.app;

import lombok.Getter;

public class MockBaseTask extends BaseTask {
	
	protected static final String TASK_NAME = "mock task";
	
	@Getter
	boolean doRunCalled = false;
	
	@Override
	public String getTaskName() {
		return TASK_NAME;
	}
	
	@Override
	public boolean doRun() throws Exception {
		doRunCalled = true;
		return true;
	}
	
}
