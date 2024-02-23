package org.openmrs.eip.app.sender.reconcile;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class SenderReconcileTaskTest {
	
	@Mock
	private SenderReconcileRepository mockRepo;
	
	private SenderReconcileTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		task = new SenderReconcileTask();
	}
	
	@Test
	public void getNextBatch_shouldFetchTheNextReconciliation() {
		setInternalState(task, SenderReconcileRepository.class, mockRepo);
		SenderReconciliation expectedRec = new SenderReconciliation();
		when(mockRepo.getReconciliation()).thenReturn(expectedRec);
		
		List<SenderReconciliation> reconciliations = task.getNextBatch();
		
		Assert.assertEquals(List.of(expectedRec), reconciliations);
		verify(mockRepo).getReconciliation();
	}
	
	@Test
	public void getNextBatch_shouldReturnEmptyListIfNoReconciliationExists() {
		setInternalState(task, SenderReconcileRepository.class, mockRepo);
		
		List<SenderReconciliation> reconciliations = task.getNextBatch();
		
		Assert.assertTrue(reconciliations.isEmpty());
		verify(mockRepo).getReconciliation();
	}
	
	@Test
	public void doRun_shouldRunAndReturnFalse() throws Exception {
		task = Mockito.spy(task);
		doNothing().when(task).invokeSuper();
		
		Assert.assertTrue(task.doRun());
		
		Mockito.verify(task).invokeSuper();
	}
	
}
