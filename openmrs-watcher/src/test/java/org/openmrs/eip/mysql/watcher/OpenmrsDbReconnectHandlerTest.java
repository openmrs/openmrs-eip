package org.openmrs.eip.mysql.watcher;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.Logger;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PrepareForTest({ Utils.class, FailureTolerantMySqlConnector.class })
public class OpenmrsDbReconnectHandlerTest {
	
	@Mock
	private CamelContext mockContext;
	
	@Mock
	private RouteController mockRouteController;
	
	@Mock
	private Logger logger;
	
	private AutoCloseable openMocksAutoCloseable;
	
	private ImmutableList<AutoCloseable> staticMocksAutoCloseable = ImmutableList.of();
	
	@BeforeAll
	public void setup() {
		staticMocksAutoCloseable = ImmutableList.of(mockStatic(Utils.class),
		    mockStatic(FailureTolerantMySqlConnector.class));
	}
	
	@BeforeEach
	public void beforeEach() throws Exception {
		when(mockContext.getRouteController()).thenReturn(mockRouteController);
	}
	
	@AfterAll
	public void afterAll() throws Exception {
		Utils.shutdown();
		for (AutoCloseable closeable : staticMocksAutoCloseable) {
			closeable.close();
		}
	}
	
	@Disabled
	public void run_shouldStopTheWatchDogExecutorAndResumeTheDebeziumRoute() throws Exception {
		OpenmrsDbReconnectHandler handler = new OpenmrsDbReconnectHandler(mockContext);
		
		handler.run();
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		mockRouteController.resumeRoute(DEBEZIUM_ROUTE_ID);
		
		verify(FailureTolerantMySqlConnector.class);
		verify(mockRouteController).resumeRoute(DEBEZIUM_ROUTE_ID);
	}
	
	@Test
	public void run_shouldFailIfTheDebeziumRouteCannotBeResumed() throws Exception {
		OpenmrsDbReconnectHandler handler = new OpenmrsDbReconnectHandler(mockContext);
		doThrow(new EIPException("test")).when(mockRouteController).resumeRoute(DEBEZIUM_ROUTE_ID);
		
		handler.run();
		
		verify(FailureTolerantMySqlConnector.class);
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		verify(Utils.class);
		Utils.shutdown();
	}
}
