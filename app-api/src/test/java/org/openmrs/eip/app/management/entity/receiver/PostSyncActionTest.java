package org.openmrs.eip.app.management.entity.receiver;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionStatus;

public class PostSyncActionTest {
	
	@Test
	public void markAsProcessedWithError_shouldSetTheStatusToFailureAndDateProcessedAndStatusMessage() {
		final String errorMsg = "some error";
		PostSyncAction a = new PostSyncAction();
		Assert.assertNotEquals(PostSyncActionStatus.FAILURE, a.getStatus());
		Assert.assertNull(a.getDateProcessed());
		Assert.assertNull(a.getStatusMessage());
		
		a.markAsProcessedWithError(errorMsg);
		
		Assert.assertEquals(PostSyncActionStatus.FAILURE, a.getStatus());
		Date currentDate = a.getDateProcessed();
		Assert.assertTrue(a.getDateProcessed().equals(currentDate) || a.getDateProcessed().after(currentDate));
		Assert.assertEquals(errorMsg, a.getStatusMessage());
	}
	
	@Test
	public void markAsCompleted_shouldSetTheStatusToSuccessAndDateProcessedAndClearStatusMessage() {
		PostSyncAction a = new PostSyncAction();
		a.markAsProcessedWithError("some error");
		Date oldDate = a.getDateProcessed();
		
		a.markAsCompleted();
		
		Assert.assertEquals(PostSyncActionStatus.SUCCESS, a.getStatus());
		Assert.assertTrue(a.getDateProcessed().equals(oldDate) || a.getDateProcessed().after(oldDate));
		Assert.assertNull(a.getStatusMessage());
	}
	
	@Test
	public void isCompleted_shouldReturnFalseForNewMessage() {
		Assert.assertFalse(new PostSyncAction().isCompleted());
	}
	
	@Test
	public void isCompleted_shouldReturnFalseForFailedMessage() {
		PostSyncAction a = new PostSyncAction();
		a.markAsProcessedWithError("some error");
		Assert.assertFalse(a.isCompleted());
	}
	
	@Test
	public void isCompleted_shouldReturnTrueForSuccessMessage() {
		PostSyncAction a = new PostSyncAction();
		a.markAsCompleted();
		Assert.assertTrue(a.isCompleted());
	}
	
}
