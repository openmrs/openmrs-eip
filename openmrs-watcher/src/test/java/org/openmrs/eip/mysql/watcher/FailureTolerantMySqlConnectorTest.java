package org.openmrs.eip.mysql.watcher;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.mysql.watcher.FailureTolerantMySqlConnector.EXECUTOR_NAME;
import static org.openmrs.eip.mysql.watcher.FailureTolerantMySqlConnector.EXECUTOR_SHUTDOWN_TIMEOUT;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.RouteController;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.AppContext;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
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
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.mockStatic(AppContext.class);
		when(AppContext.getBean(CamelContext.class)).thenReturn(mockContext);
        when(mockContext.getRouteController()).thenReturn(mockRouteController);
		when(AppContext.getBean(OpenmrsDbReconnectWatchDog.class)).thenReturn(mockWatchdog);
		when(AppContext.getBean(Environment.class)).thenReturn(mockEnv);
		when(mockEnv.getProperty(PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY, Long.class,
		    DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY)).thenReturn(DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY);
		connector = Mockito.spy(connector);
		when(connector.createExecutor()).thenReturn(mockExecutor);
	}
	
	@After
	public void tearDown() {
		Whitebox.setInternalState(FailureTolerantMySqlConnector.class, "executor", (Object) null);
	}
	
	@Test
	public void stop_shouldSuspendTheDebeziumRouteAndStartTheWatchDog() throws Exception {
		connector.stop();
		
		verify(mockRouteController).suspendRoute(DEBEZIUM_ROUTE_ID);
		verify(mockExecutor).scheduleWithFixedDelay(mockWatchdog, DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY,
		    DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY, TimeUnit.MILLISECONDS);
	}
	
	@Test
	public void stop_shouldStopTheWatchDogExecutorAndDoNothingIfTheApplicationIsShutdown() {
		when(Utils.isShuttingDown()).thenReturn(true);
		Whitebox.setInternalState(FailureTolerantMySqlConnector.class, "executor", mockExecutor);
		
		connector.stop();
		
		verifyNoInteractions(mockRouteController);
		PowerMockito.verifyStatic(Utils.class);
		Utils.shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
	@Test
	public void stop_shouldFailIfTheDebeziumRouteCannotBeSuspended() throws Exception {
		Mockito.doThrow(new EIPException("test")).when(mockRouteController).suspendRoute(DEBEZIUM_ROUTE_ID);
		
		connector.stop();
		
		verifyNoInteractions(mockExecutor);
		PowerMockito.verifyStatic(Utils.class);
		Utils.shutdown();
	}
	
	@Test
	public void stop_shouldFailIfTheWatchDogCannotBeStarted() {
		Mockito.doThrow(new EIPException("test")).when(mockEnv).getProperty(PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY,
		    Long.class, DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY);
		
		connector.stop();
		
		verifyNoInteractions(mockExecutor);
		PowerMockito.verifyStatic(Utils.class);
		Utils.shutdown();
		PowerMockito.verifyStatic(AppContext.class);
		AppContext.getBean(OpenmrsDbReconnectWatchDog.class);
	}
	
	@Test
	public void stopReconnectWatchDogExecutor_shouldDoNothingIfTheTheExecutorIsNull() {
		Assert.assertNull(Whitebox.getInternalState(FailureTolerantMySqlConnector.class, "executor"));
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		
		PowerMockito.verifyStatic(Utils.class, never());
		Utils.shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
	@Test
	public void stopReconnectWatchDogExecutor_shouldDoNothingIfTheTheExecutorIsTerminated() {
		Whitebox.setInternalState(FailureTolerantMySqlConnector.class, "executor", mockExecutor);
		when(mockExecutor.isTerminated()).thenReturn(true);
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		
		PowerMockito.verifyStatic(Utils.class, never());
		Utils.shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
	@Test
	public void stopReconnectWatchDogExecutor_shouldStopTheExecutorIfItIsNotTerminated() {
		Whitebox.setInternalState(FailureTolerantMySqlConnector.class, "executor", mockExecutor);
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		
		PowerMockito.verifyStatic(Utils.class);
		Utils.shutdownExecutor(mockExecutor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
	}
	
}
