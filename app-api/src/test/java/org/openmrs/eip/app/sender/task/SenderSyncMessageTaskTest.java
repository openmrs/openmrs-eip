package org.openmrs.eip.app.sender.task;

import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.repository.SenderSyncMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, AppUtils.class })
public class SenderSyncMessageTaskTest {
	
	@Mock
	private SenderSyncMessageRepository mockRepo;
	
	@Mock
	private Pageable mockPage;
	
	private SenderSyncMessageTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(AppUtils.class);
		when(AppUtils.getTaskPage()).thenReturn(mockPage);
		task = new SenderSyncMessageTask();
		setInternalState(task, SenderSyncMessageRepository.class, mockRepo);
	}
	
	@Test
	public void getNextBatch_shouldReadTheNextPageOfMessagesToSend() {
		List<SenderSyncMessage> expectedMessages = List.of(new SenderSyncMessage());
		Mockito.when(mockRepo.getNewSyncMessages(mockPage)).thenReturn(expectedMessages);
		
		List<SenderSyncMessage> actualMessages = task.getNextBatch();
		
		Assert.assertEquals(expectedMessages, actualMessages);
	}
	
}
