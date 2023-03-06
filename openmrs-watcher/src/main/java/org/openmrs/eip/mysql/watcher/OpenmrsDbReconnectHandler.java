package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.Constants.WATCHDOG_EXECUTOR_SHUTDOWN_TIMEOUT;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;

import java.util.concurrent.ExecutorService;

import org.apache.camel.CamelContext;
import org.openmrs.eip.AppContext;
import org.openmrs.eip.Constants;
import org.openmrs.eip.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Called by the {@link OpenmrsDbReconnectWatchDog} upon reconnection to the OpenMRS DB, this
 * handler responsible to terminate the executor for the watchdog and restarts the debezium route
 */
public class OpenmrsDbReconnectHandler implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenmrsDbReconnectHandler.class);
	
	private ExecutorService executor;
	
	public OpenmrsDbReconnectHandler(ExecutorService executor) {
		this.executor = executor;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("openmrs-db-reconnect-handler");
		
		Utils.shutdownExecutor(executor, Constants.WATCHDOG_EXECUTOR_NAME, WATCHDOG_EXECUTOR_SHUTDOWN_TIMEOUT);
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Resuming debezium route");
		}
		
		try {
			AppContext.getBean(CamelContext.class).getRouteController().resumeRoute(DEBEZIUM_ROUTE_ID);
		}
		catch (Exception e) {
			LOGGER.error("Failed to resume debezium route", e);
			Utils.shutdown();
		}
	}
	
}
