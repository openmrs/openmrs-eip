package org.openmrs.eip.app.receiver;

import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class SyncedMessageDeleterTest {
	
	@Mock
	private SyncedMessageDeletingProcessor mockProcessor;
	
	@Mock
	private SyncedMessageRepository mockRepo;
	
	@Mock
	private SiteInfo mockSite;
	
	@Mock
	private Pageable mockPage;
	
	private SyncedMessageDeleter deleter;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		setInternalState(BaseSiteRunnable.class, "page", mockPage);
		deleter = new SyncedMessageDeleter(mockSite);
		Whitebox.setInternalState(deleter, SyncedMessageDeletingProcessor.class, mockProcessor);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
		setInternalState(BaseSiteRunnable.class, "page", (Object) null);
	}
	
	@Test
	public void process() throws Exception {
		List<SyncedMessage> msgs = Collections.singletonList(new SyncedMessage());
		
		deleter.process(msgs);
		
		Mockito.verify(mockProcessor).processWork(msgs);
	}
	
	@Test
	public void getNextBatch_shouldInvokeTheRepoToFetchTheNextBatchOfMessagesForDeleting() {
		setInternalState(deleter, SyncedMessageRepository.class, mockRepo);
		
		deleter.getNextBatch();
		
		verify(mockRepo).getBatchOfMessagesForDeleting(mockSite, mockPage);
	}
	
}
