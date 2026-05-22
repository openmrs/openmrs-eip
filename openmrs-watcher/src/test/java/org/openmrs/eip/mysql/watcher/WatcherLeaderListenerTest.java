package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_LEADER_ELECTION_ENABLED;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.spi.RouteController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;

public class WatcherLeaderListenerTest {
	
	@Mock
	private CamelContext mockCamelContext;
	
	@Mock
	private RouteController mockRouteController;
	
	@Mock
	private Route mockRoute;
	
	private WatcherLeaderListener listener;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		listener = new WatcherLeaderListener(mockCamelContext);
		Mockito.when(mockCamelContext.getRouteController()).thenReturn(mockRouteController);
		WatcherLeaderListener.reset();
	}
	
	@Test
	public void onGranted_shouldStartTheDebeziumRouteIfItExists() throws Exception {
		Mockito.when(mockCamelContext.getRoute(DEBEZIUM_ROUTE_ID)).thenReturn(mockRoute);
		Assertions.assertFalse(WatcherLeaderListener.isLeader());
		
		listener.onGranted(Mockito.mock(OnGrantedEvent.class));
		
		Assertions.assertTrue(WatcherLeaderListener.isLeader());
		Mockito.verify(mockRouteController).startRoute(DEBEZIUM_ROUTE_ID);
	}
	
	@Test
	public void onGranted_shouldNotStartTheDebeziumRouteIfItDoesNotExist() throws Exception {
		Mockito.when(mockCamelContext.getRoute(DEBEZIUM_ROUTE_ID)).thenReturn(null);
		Assertions.assertFalse(WatcherLeaderListener.isLeader());
		
		listener.onGranted(Mockito.mock(OnGrantedEvent.class));
		
		Assertions.assertTrue(WatcherLeaderListener.isLeader());
		Mockito.verify(mockRouteController, Mockito.never()).startRoute(Mockito.anyString());
	}
	
	@Test
	public void onRevoked_shouldStopTheDebeziumRouteIfItExists() throws Exception {
		Mockito.when(mockCamelContext.getRoute(DEBEZIUM_ROUTE_ID)).thenReturn(mockRoute);
		// Manually set leader to true
		listener.onGranted(Mockito.mock(OnGrantedEvent.class));
		Assertions.assertTrue(WatcherLeaderListener.isLeader());
		
		listener.onRevoked(Mockito.mock(OnRevokedEvent.class));
		
		Assertions.assertFalse(WatcherLeaderListener.isLeader());
		Mockito.verify(mockRouteController).stopRoute(DEBEZIUM_ROUTE_ID);
	}
	
	@Test
	public void onRevoked_shouldNotStopTheDebeziumRouteIfItDoesNotExist() throws Exception {
		Mockito.when(mockCamelContext.getRoute(DEBEZIUM_ROUTE_ID)).thenReturn(null);
		// Manually set leader to true
		listener.onGranted(Mockito.mock(OnGrantedEvent.class));
		Assertions.assertTrue(WatcherLeaderListener.isLeader());
		
		listener.onRevoked(Mockito.mock(OnRevokedEvent.class));
		
		Assertions.assertFalse(WatcherLeaderListener.isLeader());
		Mockito.verify(mockRouteController, Mockito.never()).stopRoute(Mockito.anyString());
	}
	
	@Test
	public void onRouteRegistered_shouldStartTheDebeziumRouteIfIsLeader() throws Exception {
		Mockito.when(mockCamelContext.getRoute(DEBEZIUM_ROUTE_ID)).thenReturn(mockRoute);
		// Manually set leader to true
		listener.onGranted(Mockito.mock(OnGrantedEvent.class));
		Mockito.clearInvocations(mockRouteController);
		
		WatcherLeaderListener.onRouteRegistered(mockCamelContext);
		
		Mockito.verify(mockRouteController).startRoute(DEBEZIUM_ROUTE_ID);
	}
	
	@Test
	public void onRouteRegistered_shouldNotStartTheDebeziumRouteIfIsNotLeader() throws Exception {
		WatcherLeaderListener.onRouteRegistered(mockCamelContext);
		
		Mockito.verify(mockRouteController, Mockito.never()).startRoute(Mockito.anyString());
	}
	
}
