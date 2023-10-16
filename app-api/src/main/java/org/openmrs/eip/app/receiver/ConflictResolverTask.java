package org.openmrs.eip.app.receiver;

import org.openmrs.eip.component.SyncContext;

/**
 * Fetches all the existing conflicts and attempts to resolve those where applicable by delegating
 * to the {@link ConflictResolvingProcessor}.
 */
public class ConflictResolverTask extends BaseConflictTask<ConflictResolvingProcessor> {
	
	private static class InstanceHolder {
		
		private static final ConflictResolverTask INSTANCE = new ConflictResolverTask();
		
	}
	
	public static ConflictResolverTask getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	private ConflictResolverTask() {
		super(SyncContext.getBean(ConflictResolvingProcessor.class));
	}
	
	@Override
	public String getTaskName() {
		return "conflict resolver task";
	}
	
}
