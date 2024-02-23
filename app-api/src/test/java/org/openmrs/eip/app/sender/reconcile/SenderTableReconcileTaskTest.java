package org.openmrs.eip.app.sender.reconcile;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class SenderTableReconcileTaskTest {
	
	@Mock
	private SenderTableReconcileRepository mockRepo;
	
	private SenderTableReconcileTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		task = new SenderTableReconcileTask();
	}
	
	@Test
	public void getNextBatch_shouldReadTheNextPageOfReconcileMessages() {
		setInternalState(task, SenderTableReconcileRepository.class, mockRepo);
		List expectedRecs = List.of(new SenderTableReconciliation());
		when(mockRepo.getIncompleteReconciliations()).thenReturn(expectedRecs);
		
		List<SenderTableReconciliation> recs = task.getNextBatch();
		
		Assert.assertEquals(expectedRecs, recs);
		verify(mockRepo).getIncompleteReconciliations();
	}
	
}
