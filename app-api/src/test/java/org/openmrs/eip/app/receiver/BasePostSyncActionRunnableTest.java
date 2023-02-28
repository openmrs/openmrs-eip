package org.openmrs.eip.app.receiver;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class })
public class BasePostSyncActionRunnableTest {
	
	private MockPostSyncActionRunnable runnable;
	
	@Mock
	private PostSyncActionRepository mockRepo;
	
	@Mock
	private SiteInfo mockSite;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		runnable = new MockPostSyncActionRunnable(mockSite);
		Whitebox.setInternalState(runnable, PostSyncActionRepository.class, mockRepo);
	}
	
	@Test
	public void doRun_shouldDoNothingIfThereNoActionsAreFound() throws Exception {
		runnable = Mockito.spy(runnable);
		when(runnable.getNextBatch()).thenReturn(emptyList());
		
		Assert.assertTrue(runnable.doRun());
		
		verify(runnable, never()).process(anyList());
	}
	
	@Test
	public void doRun_shouldReadAndProcessTheNextBatchOfActions() throws Exception {
		runnable = Mockito.spy(runnable);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		when(runnable.getNextBatch()).thenReturn(actions);
		
		Assert.assertFalse(runnable.doRun());
		
		verify(runnable).process(actions);
	}
	
	@Test
	public void getNextBatch_shouldInvokeTheRepoToFetchTheNextBatchOfUnprocessedSyncResponseActions() {
		runnable.getNextBatch();
		
		verify(mockRepo).getOrderedBatchOfPendingActions(mockSite, runnable.actionType, runnable.pageable);
	}
	
}
