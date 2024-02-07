package org.openmrs.eip.app.receiver.reconcile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.repository.ReconciliationMsgRepository;
import org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class ReceiverReconcileMsgTaskTest {
	
	@Mock
	private ReconciliationMsgRepository mockRepo;
	
	@Mock
	private Page mockPage;
	
	private ReceiverReconcileMsgTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", true);
		task = new ReceiverReconcileMsgTask();
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", false);
	}
	
	@Test
	public void getNextBatch_shouldReadTheNextPageOfReconcileMessages() {
		setInternalState(task, ReconciliationMsgRepository.class, mockRepo);
		when(mockRepo.findAll(any(Pageable.class))).thenReturn(mockPage);
		List mockMessages = List.of(new ReconciliationMessage());
		when(mockPage.getContent()).thenReturn(mockMessages);
		
		List messages = task.getNextBatch();
		
		Assert.assertEquals(mockMessages, messages);
		ArgumentCaptor<Pageable> pageArgCaptor = ArgumentCaptor.forClass(Pageable.class);
		verify(mockRepo).findAll(pageArgCaptor.capture());
		Assert.assertEquals(Runtime.getRuntime().availableProcessors(), pageArgCaptor.getValue().getPageSize());
	}
	
}
