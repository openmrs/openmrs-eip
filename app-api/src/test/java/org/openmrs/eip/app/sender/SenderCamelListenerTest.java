package org.openmrs.eip.app.sender;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppUtils.class)
public class SenderCamelListenerTest {
	
	@Mock
	private ScheduledExecutorService mockExecutor;
	
	@Mock
	private File mockDbzmOffsetFile;
	
	private SenderCamelListener listener;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(AppUtils.class);
		listener = new SenderCamelListener(mockExecutor);
	}
	
	@Test
	public void notify_shouldNotStartTheBinlogTaskIfNotEnabled() throws Exception {
		listener.notify((CamelEvent.CamelContextStartedEvent) () -> null);
		
		Mockito.verifyNoInteractions(mockExecutor);
	}
	
	@Test
	public void notify_shouldStartTheBinlogTaskIfEnabledForStartedEvent() throws Exception {
		int maxKeepCount = 5;
		long initialDelay = 2000;
		long delay = 1000;
		setInternalState(listener, "binlogPurgerEnabled", true);
		setInternalState(listener, "debeziumOffsetFile", mockDbzmOffsetFile);
		setInternalState(listener, "binlogMaxKeepCount", maxKeepCount);
		setInternalState(listener, "initialDelayBinlogPurger", initialDelay);
		setInternalState(listener, "delayBinlogPurger", delay);
		
		listener.notify((CamelEvent.CamelContextStartedEvent) () -> null);
		
		ArgumentCaptor<BinlogPurgingTask> taskCaptor = ArgumentCaptor.forClass(BinlogPurgingTask.class);
		verify(mockExecutor).scheduleWithFixedDelay(taskCaptor.capture(), eq(initialDelay), eq(delay), eq(MILLISECONDS));
		BinlogPurgingTask task = taskCaptor.getValue();
		Assert.assertEquals(mockDbzmOffsetFile, getInternalState(task, "debeziumOffsetFile"));
		Assert.assertEquals(maxKeepCount, (int) getInternalState(task, "maxKeepCount"));
	}
	
	@Test
	public void notify_shouldStopTheExecutorForStoppingEvent() throws Exception {
		listener.notify((CamelEvent.CamelContextStoppingEvent) () -> null);
		
		PowerMockito.verifyStatic(AppUtils.class);
		AppUtils.shutdownExecutor(mockExecutor, "scheduled", false);
	}
	
}
