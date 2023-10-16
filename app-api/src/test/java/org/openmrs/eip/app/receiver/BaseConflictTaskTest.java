package org.openmrs.eip.app.receiver;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, AppUtils.class })
public class BaseConflictTaskTest {
	
	@Mock
	private ConflictRepository mockRepo;
	
	@Mock
	private BasePureParallelQueueProcessor mockProcessor;
	
	private BaseConflictTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(AppUtils.class);
		task = new MockConflictTask(mockProcessor);
		setInternalState(task, ConflictRepository.class, mockRepo);
	}
	
	@After
	public void tearDown() {
		setInternalState(task, "started", false);
	}
	
	@Test
	public void doRun_shouldInvokeTheProcessorToVerifyTheConflicts() throws Exception {
		Pageable pageable = PageRequest.of(0, 2);
		when(AppUtils.getTaskPage()).thenReturn(pageable);
		when(mockRepo.getConflictIds()).thenReturn(asList(1L, 2L, 3L, 4L, 5L, 6L, 7L));
		ConflictQueueItem c1 = new ConflictQueueItem();
		c1.setId(1L);
		ConflictQueueItem c2 = new ConflictQueueItem();
		c2.setId(2L);
		ConflictQueueItem c3 = new ConflictQueueItem();
		c3.setId(3L);
		ConflictQueueItem c4 = new ConflictQueueItem();
		c4.setId(4L);
		ConflictQueueItem c5 = new ConflictQueueItem();
		c5.setId(5L);
		ConflictQueueItem c6 = new ConflictQueueItem();
		c6.setId(6L);
		ConflictQueueItem c7 = new ConflictQueueItem();
		c7.setId(7L);
		when(mockRepo.findAllById(asList(1L, 2L))).thenReturn(asList(c1, c2));
		when(mockRepo.findAllById(asList(3L, 4L))).thenReturn(asList(c3, c4));
		when(mockRepo.findAllById(asList(5L, 6L))).thenReturn(asList(c5, c6));
		when(mockRepo.findAllById(asList(7L))).thenReturn(asList(c7));
		
		Assert.assertTrue(task.doRun());
		
		verify(mockProcessor).processWork(asList(c1, c2));
		verify(mockProcessor).processWork(asList(c3, c4));
		verify(mockProcessor).processWork(asList(c5, c6));
		verify(mockProcessor).processWork(asList(c7));
		
	}
	
	@Test
	public void doRun_shouldDoNothingIfAlreadyStarted() throws Exception {
		setInternalState(task, "started", true);
		
		Assert.assertTrue(task.doRun());
		
		Mockito.verifyNoInteractions(mockRepo);
	}
	
}
