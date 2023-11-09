package org.openmrs.eip.mysql.watcher;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

@ExtendWith(MockitoExtension.class)
@PrepareForTest({ Utils.class, FailureTolerantMySqlConnector.class })
public class OpenmrsDbReconnectHandlerTest {
	
	@Mock
	private CamelContext mockContext;
	
	@Mock
	private RouteController mockRouteController;
	
	private static ImmutableList<AutoCloseable> staticMocksAutoCloseable = ImmutableList.of();
	
	@BeforeAll
	public static void setupClass() {
		staticMocksAutoCloseable = ImmutableList.of(mockStatic(Utils.class),
		    mockStatic(FailureTolerantMySqlConnector.class));
	}
	
	@AfterAll
	public static void tearDownClass() throws Exception {
		for (AutoCloseable closeable : staticMocksAutoCloseable) {
			closeable.close();
		}
		validateMockitoUsage();
	}
	
	@BeforeEach
	public void setup() throws Exception {
		when(mockContext.getRouteController()).thenReturn(mockRouteController);
		Mockito.doThrow(new EIPException("test")).when(mockRouteController).resumeRoute(DEBEZIUM_ROUTE_ID);
	}
	
	@Test
	public void run_shouldStopTheWatchDogExecutorAndResumeTheDebeziumRoute() throws Exception {
		OpenmrsDbReconnectHandler handler = new OpenmrsDbReconnectHandler(mockContext);
		
		handler.run();
		
		verify(FailureTolerantMySqlConnector.class, times(2));
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		verify(mockRouteController).resumeRoute(DEBEZIUM_ROUTE_ID);
		Utils.shutdown();
	}
	
	@Test
	public void run_shouldFailIfTheDebeziumRouteCannotBeResumed() throws Exception {
		OpenmrsDbReconnectHandler handler = new OpenmrsDbReconnectHandler(mockContext);
		
		handler.run();
		
		verify(FailureTolerantMySqlConnector.class, times(1));
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		verify(Utils.class, times(1));
		Utils.shutdown();
	}
}
