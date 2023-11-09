package org.openmrs.eip.mysql.watcher;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.openmrs.eip.Utils.shutdownExecutor;
import static org.openmrs.eip.mysql.watcher.FailureTolerantMySqlConnector.EXECUTOR_NAME;
import static org.openmrs.eip.mysql.watcher.FailureTolerantMySqlConnector.EXECUTOR_SHUTDOWN_TIMEOUT;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY;
import static org.powermock.reflect.Whitebox.setInternalState;
import static org.powermock.reflect.internal.WhiteboxImpl.getInternalState;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.eip.AppContext;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

@ExtendWith(SpringExtension.class)
@PrepareForTest({ Utils.class, AppContext.class })
public class FailureTolerantMySqlConnectorTest {
	
	@Mock
	private CamelContext mockContext;
	
	@Mock
	private OpenmrsDbReconnectWatchDog mockWatchdog;
	
	@Mock
	private RouteController mockRouteController;
	
	@Mock
	private ScheduledExecutorService mockExecutor;
	
	@Mock
	private Environment mockEnv;
	
	private FailureTolerantMySqlConnector connector = new FailureTolerantMySqlConnector();
	
	private static ImmutableList<AutoCloseable> staticMocksAutoCloseable = ImmutableList.of();
	
	private static AutoCloseable initMocksAutoCloseable;
	
	@BeforeAll
	public static void setup() {
		staticMocksAutoCloseable = ImmutableList.of(mockStatic(Utils.class), mockStatic(AppContext.class));
		initMocksAutoCloseable = MockitoAnnotations.openMocks(FailureTolerantMySqlConnectorTest.class);
	}
	
	@AfterAll
	public static void tearDown() throws Exception {
		setInternalState(FailureTolerantMySqlConnector.class, "executor", (Object) null);
		for (AutoCloseable closeable : staticMocksAutoCloseable) {
			closeable.close();
		}
		initMocksAutoCloseable.close();
		validateMockitoUsage();
	}
	
	@BeforeEach
	public void beforeEachTest() {
		when(AppContext.getBean(CamelContext.class)).thenReturn(mockContext);
		when(mockContext.getRouteController()).thenReturn(mockRouteController);
		when(AppContext.getBean(OpenmrsDbReconnectWatchDog.class)).thenReturn(mockWatchdog);
		when(AppContext.getBean(Environment.class)).thenReturn(mockEnv);
		when(mockEnv.getProperty(PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY, Long.class,
		    DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY)).thenReturn(DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY);
		connector = Mockito.spy(connector);
		when(connector.createExecutor()).thenReturn(mockExecutor);
	}
	
	@AfterEach
	public void afterEachTest() {
		setInternalState(FailureTolerantMySqlConnector.class, "executor", (Object) null);
	}
	
	@Test
	public void stop_shouldSuspendTheDebeziumRouteAndStartTheWatchDog() throws Exception {
		when(Utils.isShuttingDown()).thenReturn(false);
		connector.stop();
		
		verify(mockRouteController).suspendRoute(DEBEZIUM_ROUTE_ID);
		verify(mockExecutor).scheduleWithFixedDelay(mockWatchdog, DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY,
		    DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void stop_shouldStopTheWatchDogExecutorAndDoNothingIfTheApplicationIsShutdown() {
		when(Utils.isShuttingDown()).thenReturn(true);
		setInternalState(FailureTolerantMySqlConnector.class, "executor", mockExecutor);
		
		connector.stop();
		
		verifyNoInteractions(mockRouteController);
		verify(Utils.class);
		shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
	@Test
	public void stop_shouldFailIfTheDebeziumRouteCannotBeSuspended() throws Exception {
		Mockito.doThrow(new EIPException("test")).when(mockRouteController).suspendRoute(DEBEZIUM_ROUTE_ID);
		setInternalState(FailureTolerantMySqlConnector.class, "executor", mockExecutor);
		when(Utils.isShuttingDown()).thenReturn(false);
		
		connector.stop();
		
		verifyNoInteractions(mockExecutor);
		Utils.shutdown();
	}
	
	@Test
	public void stop_shouldFailIfTheWatchDogCannotBeStarted() throws Exception {
		Mockito.doThrow(new EIPException("test")).when(mockEnv).getProperty(PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY,
		    Long.class, DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY);
		when(Utils.isShuttingDown()).thenReturn(false);
		
		connector.stop();
		
		verifyNoInteractions(mockExecutor);
		verify(Utils.class, times(3));
		Utils.shutdown();
		AppContext.getBean(OpenmrsDbReconnectWatchDog.class);
	}
	
	@Test
	public void stopReconnectWatchDogExecutor_shouldDoNothingIfTheTheExecutorIsNull() {
		when(connector.createExecutor()).thenReturn(mockExecutor);
		assertNull(getInternalState(FailureTolerantMySqlConnector.class, "executor"));
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		
		verify(Utils.class, never());
		shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
	@Test
	public void stopReconnectWatchDogExecutor_shouldDoNothingIfTheTheExecutorIsTerminated() {
		setInternalState(FailureTolerantMySqlConnector.class, "executor", mockExecutor);
		when(mockExecutor.isTerminated()).thenReturn(true);
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		
		verify(Utils.class, never());
		shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
	@Test
	public void stopReconnectWatchDogExecutor_shouldStopTheExecutorIfItIsNotTerminated() {
		setInternalState(FailureTolerantMySqlConnector.class, "executor", mockExecutor);
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		
		verify(Utils.class);
		shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
}
