package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_LEADER_ELECTION_ENABLED;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = PROP_LEADER_ELECTION_ENABLED, havingValue = "true")
public class WatcherLeaderListener {
	
	private static final Logger logger = LoggerFactory.getLogger(WatcherLeaderListener.class);
	
	private static final AtomicBoolean leader = new AtomicBoolean();
	
	private static final Object LOCK = new Object();
	
	private CamelContext camelContext;
	
	public WatcherLeaderListener(CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	/**
	 * Checks if this instance is the leader.
	 * 
	 * @return true if leader otherwise false
	 */
	public static boolean isLeader() {
		return leader.get();
	}
	
	/**
	 * Resets the leader status to false, should only be called by tests.
	 */
	protected static void reset() {
		leader.set(false);
	}
	
	@EventListener
	public void onGranted(OnGrantedEvent event) throws Exception {
		synchronized (LOCK) {
			logger.info("Leadership granted, starting debezium route");
			leader.set(true);
			startRoute(camelContext);
		}
	}
	
	@EventListener
	public void onRevoked(OnRevokedEvent event) throws Exception {
		synchronized (LOCK) {
			logger.info("Leadership revoked, stopping debezium route");
			leader.set(false);
			stopRoute(camelContext);
		}
	}
	
	/**
	 * Should be called when the debezium route is registered to start it if leadership was already
	 * granted.
	 * 
	 * @param camelContext the CamelContext
	 */
	public static void onRouteRegistered(CamelContext camelContext) throws Exception {
		synchronized (LOCK) {
			if (leader.get()) {
				logger.info("Leadership already granted, starting debezium route");
				startRoute(camelContext);
			}
		}
	}
	
	private static void startRoute(CamelContext camelContext) throws Exception {
		Route route = camelContext.getRoute(DEBEZIUM_ROUTE_ID);
		if (route != null) {
			camelContext.getRouteController().startRoute(DEBEZIUM_ROUTE_ID);
		} else {
			logger.warn("Debezium route not found, it might not have been registered yet");
		}
	}
	
	private static void stopRoute(CamelContext camelContext) throws Exception {
		Route route = camelContext.getRoute(DEBEZIUM_ROUTE_ID);
		if (route != null) {
			camelContext.getRouteController().stopRoute(DEBEZIUM_ROUTE_ID);
		}
	}
	
}
