package org.openmrs.eip.app;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.event.CamelContextStartedEvent;
import org.apache.camel.impl.event.CamelContextStoppingEvent;
import org.apache.camel.spi.ShutdownStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppUtils.class)
public class BaseCamelListenerTest {
	
	@Mock
	private ThreadPoolExecutor mockSyncExecutor;
	
	@Mock
	private CamelContext mockContext;
	
	@Mock
	private ShutdownStrategy mockShutdownStrategy;
	
	@Test
	public void notify_shouldProcessStartedEvent() throws Exception {
		Mockito.when(mockContext.getShutdownStrategy()).thenReturn(mockShutdownStrategy);
		MockBaseCamelListener listener = new MockBaseCamelListener();
		
		listener.notify(new CamelContextStartedEvent(mockContext));
		
		Mockito.verify(mockShutdownStrategy).setTimeout(15);
		Mockito.verify(mockShutdownStrategy).setShutdownNowOnTimeout(true);
		Assert.assertTrue(listener.started);
		Assert.assertFalse(listener.stopped);
	}
	
	@Test
	public void notify_shouldProcessStoppingEvent() throws Exception {
		PowerMockito.mockStatic(AppUtils.class);
		MockBaseCamelListener listener = new MockBaseCamelListener();
		
		listener.notify(new CamelContextStoppingEvent(new DefaultCamelContext()));
		
		Assert.assertTrue(listener.stopped);
		Assert.assertFalse(listener.started);
		PowerMockito.verifyStatic(AppUtils.class);
		AppUtils.handleAppContextStopping();
		PowerMockito.verifyStatic(AppUtils.class);
		AppUtils.shutdownExecutor(mockSyncExecutor, "sync", false);
	}
	
	public class MockBaseCamelListener extends BaseCamelListener {
		
		private boolean started;
		
		private boolean stopped;
		
		public MockBaseCamelListener() {
			super(mockSyncExecutor);
		}
		
		@Override
		public void applicationStarted() {
			started = true;
		}
		
		@Override
		public void applicationStopped() {
			stopped = true;
		}
		
	}
	
}
