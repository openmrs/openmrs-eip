package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;

import org.apache.camel.CamelContext;
import org.openmrs.eip.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Called by the {@link OpenmrsDbReconnectWatchDog} upon reconnection to the OpenMRS DB, this
 * handler responsible to terminate the executor for the watchdog and resumes the debezium route
 */
@Component
public class OpenmrsDbReconnectHandler implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenmrsDbReconnectHandler.class);
	
	private CamelContext camelContext;
	
	public OpenmrsDbReconnectHandler(CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("openmrs-db-reconnect-handler");
		
		FailureTolerantMySqlConnector.stopReconnectWatchDogExecutor();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Resuming debezium route");
		}
		
		try {
			camelContext.getRouteController().resumeRoute(DEBEZIUM_ROUTE_ID);
		}
		catch (Exception e) {
			LOGGER.error("Failed to resume debezium route", e);
			Utils.shutdown();
		}
	}
	
}
