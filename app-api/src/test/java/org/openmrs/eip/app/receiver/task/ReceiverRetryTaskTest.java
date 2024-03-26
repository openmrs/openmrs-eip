package org.openmrs.eip.app.receiver.task;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, AppUtils.class })
public class ReceiverRetryTaskTest {
	
	@Mock
	private Pageable mockPage;
	
	@Mock
	private ReceiverRetryRepository mockRepo;
	
	private ReceiverRetryTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(AppUtils.class);
	}
	
	@Test
	public void getNextBatch_shouldFetchTheNextBatchOfRetries() {
		when(AppUtils.getTaskPage()).thenReturn(mockPage);
		task = new ReceiverRetryTask();
		setInternalState(task, ReceiverRetryRepository.class, mockRepo);
		
		task.getNextBatch();
		
		verify(mockRepo).getRetries(mockPage);
	}
	
}
