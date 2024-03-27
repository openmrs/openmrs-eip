package org.openmrs.eip.app;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppUtils.class)
public class BaseTaskTest {
	
	private MockBaseTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(AppUtils.class);
		task = new MockBaseTask();
	}
	
	@Test
	public void run_shouldNotRunIfSkipReturnsTrue() throws Exception {
		task = Mockito.spy(task);
		when(task.skip()).thenReturn(true);
		
		task.run();
		
		Mockito.verify(task, Mockito.never()).doRun();
		Mockito.verify(task, Mockito.never()).beforeStart();
		Mockito.verify(task, Mockito.never()).beforeStop();
		Assert.assertFalse(task.doRunCalled);
	}
	
	@Test
	public void run_shouldNotRunIfAppIsStopping() throws Exception {
		task = Mockito.spy(task);
		when(AppUtils.isShuttingDown()).thenReturn(true);
		
		task.run();
		
		Mockito.verify(task, Mockito.never()).doRun();
		Mockito.verify(task, Mockito.never()).beforeStart();
		Mockito.verify(task, Mockito.never()).beforeStop();
		Assert.assertFalse(task.doRunCalled);
	}
	
	@Test
	public void run_shouldRunIfSkipReturnsFalseAndAppIsNotStopping() throws Exception {
		task = Mockito.spy(task);
		final String originalThreadName = Thread.currentThread().getName();
		List<String> threadNames = new ArrayList();
		when(task.doRun()).thenAnswer((Answer) invocationOnMock -> {
			threadNames.add(Thread.currentThread().getName());
			return true;
		});
		
		task.run();
		
		Mockito.verify(task).doRun();
		Mockito.verify(task).beforeStart();
		Mockito.verify(task).beforeStop();
		Assert.assertTrue(task.doRunCalled);
		assertEquals(originalThreadName + ":" + MockBaseTask.TASK_NAME, threadNames.get(0));
		assertEquals(originalThreadName, Thread.currentThread().getName());
	}
	
	@Test
	public void run_shouldStopRunningIfAppIsStoppingWhileRunning() throws Exception {
		task = Mockito.spy(task);
		when(task.doRun()).thenReturn(false);
		final AtomicInteger callCount = new AtomicInteger(0);
		when(task.doRun()).thenAnswer((Answer) invocationOnMock -> {
			if (callCount.get() == 1) {
				when(AppUtils.isShuttingDown()).thenReturn(true);
			}
			callCount.incrementAndGet();
			return false;
		});
		
		task.run();
		
		Mockito.verify(task, times(2)).doRun();
		Mockito.verify(task).beforeStart();
		Mockito.verify(task).beforeStop();
	}
	
	@Test
	public void run_shouldStopRunningIfAnErrorIsEncounteredWhileRunning() throws Exception {
		task = Mockito.spy(task);
		final AtomicInteger callCount = new AtomicInteger();
		when(task.doRun()).thenAnswer((Answer) invocationOnMock -> {
			if (callCount.get() == 1) {
				throw new EIPException("testing");
			}
			callCount.incrementAndGet();
			return false;
		});
		
		task.run();
		
		Mockito.verify(task, times(2)).doRun();
		Mockito.verify(task).beforeStart();
		Mockito.verify(task).beforeStop();
	}
	
}
