package org.openmrs.eip.app.sender;

import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.Exchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CompletableFuture.class)
public class ChangeEventProcessorSpyTest {
	
	@Mock
	private SnapshotSavePointStore mockStore;
	
	@Mock
	private ChangeEventHandler handler;
	
	@Mock
	private ThreadPoolExecutor mockExecutor;
	
	private ChangeEventProcessor createProcessor() {
		ChangeEventProcessor processor = new ChangeEventProcessor(mockExecutor, handler);
		return processor;
	}
	
	@Test
	public void processWork_shouldSkipPersistingTheSavePointForASnapshotEventIfTheExecutorIsShutdown() throws Exception {
		Exchange e = ApiSenderTestUtils.createExchange(0, TRUE.toString(), "person");
		Mockito.when(mockStore.getSavedRowId(ArgumentMatchers.anyString())).thenReturn(null);
		ChangeEventProcessor processor = createProcessor();
		Whitebox.setInternalState(processor, "taskThreshold", 1);
		Whitebox.setInternalState(processor, SnapshotSavePointStore.class, mockStore);
		Mockito.when(mockExecutor.isShutdown()).thenReturn(true);
		processor = Mockito.spy(processor);
		Mockito.doAnswer(invocation -> {
			List<Future> futures = invocation.getArgument(0);
			//Just remove all futures so that there will be none to wait for otherwise the processor will block 
			futures.clear();
			return null;
		}).when(processor).waitForFutures(any());
		
		processor.processWork(e);
		
		Mockito.verify(mockStore, Mockito.never()).update(anyMap());
	}
	
}
