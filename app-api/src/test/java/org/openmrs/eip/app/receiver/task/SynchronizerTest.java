package org.openmrs.eip.app.receiver.task;

import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.app.receiver.BaseSiteRunnable;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class SynchronizerTest {
	
	@Mock
	private SyncMessageRepository mockRepo;
	
	@Mock
	private SiteInfo mockSite;
	
	@Mock
	private Pageable mockPage;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		Mockito.when(SyncContext.getBean(SyncMessageRepository.class)).thenReturn(mockRepo);
		Whitebox.setInternalState(BaseSiteRunnable.class, "initialized", true);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
	}
	
	@Test
	public void getNextBatch_shouldFetchNextBatchOfSyncMessages() {
		List<SyncMessage> messages = List.of(new SyncMessage());
		Mockito.when(mockRepo.getSyncMessageBySiteOrderByDateReceived(mockSite, mockPage)).thenReturn(messages);
		Synchronizer synchronizer = new Synchronizer(mockSite);
		Assert.assertEquals(messages, synchronizer.getNextBatch(mockPage));
	}
	
}
