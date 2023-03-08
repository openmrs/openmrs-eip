package org.openmrs.eip.app.receiver;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class })
public class BasePostSyncActionRunnableTest {
	
	private BasePostSyncActionRunnable runnable;
	
	@Before
	public void setup() {
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		PowerMockito.mockStatic(SyncContext.class);
		runnable = new MockPostSyncActionRunnable(null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
	}
	
	@Test
	public void doRun_shouldDoNothingIfThereNoMessagesAreFound() throws Exception {
		runnable = Mockito.spy(runnable);
		when(runnable.getNextBatch()).thenReturn(emptyList());
		
		Assert.assertTrue(runnable.doRun());
		
		verify(runnable, never()).process(anyList());
	}
	
	@Test
	public void doRun_shouldReadAndProcessTheNextBatchOfSyncedMessages() throws Exception {
		runnable = Mockito.spy(runnable);
		List<SyncedMessage> msgs = Collections.singletonList(new SyncedMessage());
		when(runnable.getNextBatch()).thenReturn(msgs);
		
		Assert.assertFalse(runnable.doRun());
		
		verify(runnable).process(msgs);
	}
	
}
