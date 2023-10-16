package org.openmrs.eip.app.receiver;

import org.openmrs.eip.component.SyncContext;

/**
 * Fetches all the existing conflicts and verifies them by delegating to the
 * {@link ConflictVerifyingProcessor}.
 */
public class ConflictVerifyingTask extends BaseConflictTask<ConflictVerifyingProcessor> {
	
	private static class InstanceHolder {
		
		private static final ConflictVerifyingTask INSTANCE = new ConflictVerifyingTask();
		
	}
	
	public static ConflictVerifyingTask getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	private ConflictVerifyingTask() {
		super(SyncContext.getBean(ConflictVerifyingProcessor.class));
	}
	
	@Override
	public String getTaskName() {
		return "conflict verifier task";
	}
	
}
