package org.openmrs.eip.mysql.watcher;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utils.class, FailureTolerantMySqlConnector.class })
public class OpenmrsDbReconnectHandlerTest {
	
	@Mock
	private CamelContext mockContext;
	
	@Mock
	private RouteController mockRouteController;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.mockStatic(FailureTolerantMySqlConnector.class);
		when(mockContext.getRouteController()).thenReturn(mockRouteController);
	}
	
	@Test
	public void run_shouldStopTheWatchDogExecutorAndResumeTheDebeziumRoute() throws Exception {
		OpenmrsDbReconnectHandler handler = new OpenmrsDbReconnectHandler(mockContext);
		
		handler.run();
		
		PowerMockito.verifyStatic(FailureTolerantMySqlConnector.class);
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		verify(mockRouteController).resumeRoute(DEBEZIUM_ROUTE_ID);
	}
	
	@Test
	public void run_shouldFailIfTheDebeziumRouteCannotBeResumed() throws Exception {
		OpenmrsDbReconnectHandler handler = new OpenmrsDbReconnectHandler(mockContext);
		Mockito.doThrow(new EIPException("test")).when(mockRouteController).resumeRoute(DEBEZIUM_ROUTE_ID);
		
		handler.run();
		
		PowerMockito.verifyStatic(FailureTolerantMySqlConnector.class);
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		PowerMockito.verifyStatic(Utils.class);
		Utils.shutdown();
	}
	
}
