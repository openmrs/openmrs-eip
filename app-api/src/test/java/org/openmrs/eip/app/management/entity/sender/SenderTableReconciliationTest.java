package org.openmrs.eip.app.management.entity.sender;

import org.junit.Assert;
import org.junit.Test;

public class SenderTableReconciliationTest {
	
	@Test
	public void isCompleted_shouldReturnTrueIfStartedAndCompleted() {
		SenderTableReconciliation rec = new SenderTableReconciliation();
		rec.setStarted(true);
		rec.setEndId(10);
		rec.setLastProcessedId(10);
		Assert.assertTrue(rec.isCompleted());
		
		rec.setEndId(0);
		rec.setLastProcessedId(0);
		Assert.assertTrue(rec.isCompleted());
	}
	
	@Test
	public void isCompleted_shouldReturnFalseIfNotStarted() {
		SenderTableReconciliation rec = new SenderTableReconciliation();
		rec.setEndId(0);
		rec.setLastProcessedId(0);
		Assert.assertFalse(rec.isCompleted());
	}
	
	@Test
	public void isCompleted_shouldReturnFalseIfStartedAndLastProcessedIdIsLessThanEndId() {
		SenderTableReconciliation rec = new SenderTableReconciliation();
		rec.setStarted(true);
		rec.setEndId(9);
		rec.setLastProcessedId(10);
		Assert.assertFalse(rec.isCompleted());
	}
	
}
