package org.openmrs.eip.app.receiver.reconcile;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class ReceiverReconcileTaskTest {
	
	@Mock
	private ReceiverReconcileRepository mockRepo;
	
	private ReceiverReconcileTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", true);
		task = new ReceiverReconcileTask();
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", false);
	}
	
	@Test
	public void getNextBatch_shouldFetchTheNextReconciliation() {
		setInternalState(task, ReceiverReconcileRepository.class, mockRepo);
		ReceiverReconciliation expectedRec = new ReceiverReconciliation();
		when(mockRepo.getReconciliation()).thenReturn(expectedRec);
		
		List<ReceiverReconciliation> reconciliations = task.getNextBatch();
		
		Assert.assertEquals(List.of(expectedRec), reconciliations);
		verify(mockRepo).getReconciliation();
	}
	
	@Test
	public void getNextBatch_shouldReturnEmptyListIfNoReconciliationExists() {
		setInternalState(task, ReceiverReconcileRepository.class, mockRepo);
		
		List<ReceiverReconciliation> reconciliations = task.getNextBatch();
		
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
