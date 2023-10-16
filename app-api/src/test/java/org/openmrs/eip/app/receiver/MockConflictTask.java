package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;

public class MockConflictTask extends BaseConflictTask<BasePureParallelQueueProcessor<ConflictQueueItem>> {
	
	public MockConflictTask(BasePureParallelQueueProcessor processor) {
		super(processor);
	}
	
	@Override
	public String getTaskName() {
		return null;
	}
	
}
