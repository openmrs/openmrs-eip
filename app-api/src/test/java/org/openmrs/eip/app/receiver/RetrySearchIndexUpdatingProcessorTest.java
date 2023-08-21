package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PersonModel;

public class RetrySearchIndexUpdatingProcessorTest {
	
	private RetrySearchIndexUpdatingProcessor processor = new RetrySearchIndexUpdatingProcessor(null);
	
	@Test
	public void getModelClassName_shouldReturnTheModelClassname() {
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setModelClassName(PersonModel.class.getName());
		assertEquals(PersonModel.class.getName(), processor.getModelClassName(retry));
	}
	
	@Test
	public void getIdentifier_shouldReturnTheIdentifier() {
		final String uuid = "some-uuid";
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		assertEquals(uuid, processor.getIdentifier(retry));
	}
	
	@Test
	public void getOperation_shouldReturnTheOperation() {
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setOperation(SyncOperation.c);
		assertEquals(SyncOperation.c, processor.getOperation(retry));
		
	}
	
}
