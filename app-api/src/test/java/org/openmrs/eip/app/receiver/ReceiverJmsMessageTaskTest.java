package org.openmrs.eip.app.receiver;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, AppUtils.class })
public class ReceiverJmsMessageTaskTest {
	
	@Mock
	private JmsMessageRepository mockRepo;
	
	@Mock
	private Pageable mockPage;
	
	private ReceiverJmsMessageTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(AppUtils.class);
	}
	
	@Test
	public void getNextBatch_shouldFetchTheNextBatchOfMessage() {
		when(AppUtils.getTaskPage()).thenReturn(mockPage);
		task = new ReceiverJmsMessageTask();
		setInternalState(task, JmsMessageRepository.class, mockRepo);
		
		task.getNextBatch();
		
		verify(mockRepo).findAllByOrderByDateCreatedAsc(mockPage);
	}
	
}
