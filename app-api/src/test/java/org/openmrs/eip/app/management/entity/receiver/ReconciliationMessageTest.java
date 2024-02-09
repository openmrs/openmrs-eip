package org.openmrs.eip.app.management.entity.receiver;

import org.junit.Assert;
import org.junit.Test;

public class ReconciliationMessageTest {
	
	@Test
	public void isCompleted_shouldReturnTrueIfBatchAndProcessedSizesMatch() {
		final int batchCount = 5;
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setBatchSize(batchCount);
		msg.setProcessedCount(batchCount);
		Assert.assertTrue(msg.isCompleted());
	}
	
	@Test
	public void isCompleted_shouldReturnFalseIfProcessedIsLessThanBatchSizeDoMatch() {
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setBatchSize(5);
		msg.setProcessedCount(4);
		Assert.assertFalse(msg.isCompleted());
	}
	
}
