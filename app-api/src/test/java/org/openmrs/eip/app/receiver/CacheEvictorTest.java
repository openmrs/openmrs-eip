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
import org.openmrs.eip.app.management.entity.SiteInfo;
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
public class CacheEvictorTest {
	
	@Mock
	private CacheEvictingProcessor mockProcessor;
	
	@Mock
	private SyncedMessageRepository mockRepo;
	
	@Mock
	private SiteInfo mockSite;
	
	@Mock
	private Pageable mockPage;
	
	private CacheEvictor evictor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		setInternalState(BaseSiteRunnable.class, "page", mockPage);
		evictor = new CacheEvictor(mockSite);
		Whitebox.setInternalState(evictor, CacheEvictingProcessor.class, mockProcessor);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
		setInternalState(BaseSiteRunnable.class, "page", (Object) null);
	}
	
	@Test
	public void process() throws Exception {
		List<SyncedMessage> msgs = Collections.singletonList(new SyncedMessage());
		
		evictor.process(msgs);
		
		Mockito.verify(mockProcessor).processWork(msgs);
	}
	
	@Test
	public void getNextBatch_shouldInvokeTheRepoToFetchTheNextBatchOfMessagesForCacheEvictions() {
		setInternalState(evictor, SyncedMessageRepository.class, mockRepo);
		
		evictor.getNextBatch();
		
		verify(mockRepo).getBatchOfMessagesForEviction(mockSite, mockPage);
	}
	
}
