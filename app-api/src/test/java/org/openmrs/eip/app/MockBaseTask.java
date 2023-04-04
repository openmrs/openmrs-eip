package org.openmrs.eip.app;

public class MockBaseTask extends BaseTask {
	
	protected static final String TASK_NAME = "mock task";
	
	@Override
	public String getTaskName() {
		return TASK_NAME;
	}
	
	@Override
	public boolean doRun() throws Exception {
		return true;
	}
	
}
