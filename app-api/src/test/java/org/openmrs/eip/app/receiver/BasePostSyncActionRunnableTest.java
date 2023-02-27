package org.openmrs.eip.app.receiver;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, ReceiverUtils.class })
public class BasePostSyncActionRunnableTest {
	
	private MockPostSyncActionRunnable runnable;
	
	@Mock
	private PostSyncActionRepository mockRepo;
	
	@Mock
	private SiteInfo mockSite;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(ReceiverUtils.class);
		runnable = new MockPostSyncActionRunnable(mockSite);
		Whitebox.setInternalState(runnable, PostSyncActionRepository.class, mockRepo);
	}
	
	@Test
	public void doRun_shouldReadAndProcessABatchOfActionsAndMarkThemAsCompleted() throws Exception {
		runnable = Mockito.spy(runnable);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		when(runnable.getNextBatch()).thenReturn(actions);
		when(runnable.process(actions)).thenReturn(actions);
		
		Assert.assertFalse(runnable.doRun());
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, true, null);
		PowerMockito.verifyStatic(ReceiverUtils.class, never());
		ReceiverUtils.updatePostSyncActionStatuses(anyList(), ArgumentMatchers.eq(false), isNull());
	}
	
	@Test
	public void doRun_shouldMarkSuccessfulActionsAsCompletedAndUnSuccessfulOnesAsFailures() throws Exception {
		runnable = Mockito.spy(runnable);
		PostSyncAction action1 = new PostSyncAction();
		action1.setId(1l);
		PostSyncAction action2 = new PostSyncAction();
		action2.setId(2l);
		PostSyncAction action3 = new PostSyncAction();
		action3.setId(3l);
		PostSyncAction action4 = new PostSyncAction();
		action4.setId(4l);
		PostSyncAction action5 = new PostSyncAction();
		action5.setId(5l);
		List<PostSyncAction> successes = Arrays.asList(action2, action4);
		List<PostSyncAction> failures = Arrays.asList(action1, action3, action5);
		List<PostSyncAction> actions = (List) CollectionUtils.union(successes, failures);
		when(runnable.getNextBatch()).thenReturn(actions);
		when(runnable.process(actions)).thenReturn(successes);
		
		Assert.assertFalse(runnable.doRun());
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(successes, true, null);
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(failures, false, null);
	}
	
	@Test
	public void doRun_shouldUnsuccessfullyProcessActionsAndMarkThemAsFailedWhenNoExceptionIsEncountered() throws Exception {
		runnable = Mockito.spy(runnable);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		when(runnable.getNextBatch()).thenReturn(actions);
		when(runnable.process(actions)).thenReturn(emptyList());
		
		Assert.assertFalse(runnable.doRun());
		
		PowerMockito.verifyStatic(ReceiverUtils.class, never());
		ReceiverUtils.updatePostSyncActionStatuses(anyList(), ArgumentMatchers.eq(true), isNull());
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, false, null);
	}
	
	@Test
	public void doRun_shouldReadAndProcessABatchOfActionsAndMarkThemAsFailedIfAnErrorOccurs() throws Exception {
		runnable = Mockito.spy(runnable);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		when(runnable.getNextBatch()).thenReturn(actions);
		final String errMsg = "Testing";
		EIPException e = new EIPException(errMsg);
		Mockito.doThrow(e).when(runnable).process(actions);
		
		Assert.assertFalse(runnable.doRun());
		
		PowerMockito.verifyStatic(ReceiverUtils.class, never());
		ReceiverUtils.updatePostSyncActionStatuses(anyList(), ArgumentMatchers.eq(true), isNull());
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, false, e.toString());
	}
	
	@Test
	public void doRun_shouldSetStatusMessageToTheRootCauseIfAnErrorOccurs() throws Exception {
		runnable = Mockito.spy(runnable);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		when(runnable.getNextBatch()).thenReturn(actions);
		final String rootCauseMsg = "test root error";
		Exception root = new ActiveMQException(rootCauseMsg);
		Exception e = new EIPException("test1", new Exception("test2", root));
		Mockito.doThrow(e).when(runnable).process(actions);
		
		Assert.assertFalse(runnable.doRun());
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, false, root.toString());
	}
	
	@Test
	public void doRun_shouldTruncateTheErrorMessageIfLongerThan1024() throws Exception {
		runnable = Mockito.spy(runnable);
		List<PostSyncAction> actions = Collections.singletonList(new PostSyncAction());
		when(runnable.getNextBatch()).thenReturn(actions);
		final String errMsg = RandomStringUtils.randomAscii(1025);
		EIPException e = new EIPException(errMsg);
		Mockito.doThrow(e).when(runnable).process(actions);
		
		Assert.assertFalse(runnable.doRun());
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.updatePostSyncActionStatuses(actions, false, e.toString().substring(0, 1024));
	}
	
	@Test
	public void doRun_shouldDoNothingIfThereNoActionsAreFound() throws Exception {
		runnable = Mockito.spy(runnable);
		when(runnable.getNextBatch()).thenReturn(emptyList());
		
		Assert.assertTrue(runnable.doRun());
		
		verify(runnable, never()).process(anyList());
		PowerMockito.verifyZeroInteractions(ReceiverUtils.class);
	}
	
	@Test
	public void getNextBatch_shouldInvokeTheRepoToFetchTheNextBatchOfUnprocessedSyncResponseActions() {
		runnable.getNextBatch();
		
		verify(mockRepo).getBatchOfPendingActions(mockSite, runnable.getActionType(), runnable.getPageable());
	}
	
}
