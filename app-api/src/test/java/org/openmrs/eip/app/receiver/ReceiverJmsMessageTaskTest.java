package org.openmrs.eip.app.receiver;

import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.PageImpl;
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
		List<JmsMessage> expected = List.of(new JmsMessage(), new JmsMessage(), new JmsMessage());
		when(mockRepo.findAll(mockPage)).thenReturn(new PageImpl<>(expected, Pageable.ofSize(2), 3));
		
		List<JmsMessage> actual = task.getNextBatch();
		
		Assert.assertEquals(expected, actual);
	}
	
}
