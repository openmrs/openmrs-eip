package org.openmrs.eip.app.receiver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReceiverUtils.class)
public class RetrySearchIndexUpdatingProcessorTest {
	
	@Test
	public void convertBody_shouldGenerateTheOpenmrsSearchIndexUpdatePayload() {
		final String uuid = "some-uuid";
		final SyncOperation op = SyncOperation.c;
		final String modelClass = PersonModel.class.getName();
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		retry.setModelClassName(modelClass);
		retry.setOperation(op);
		PowerMockito.mockStatic(ReceiverUtils.class);
		
		new RetrySearchIndexUpdatingProcessor(null).convertBody(retry);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.generateSearchIndexUpdatePayload(modelClass, uuid, op);
	}
}
