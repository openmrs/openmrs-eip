package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.openmrs.eip.AppContext;
import org.openmrs.eip.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import io.debezium.connector.mysql.MySqlConnector;

/**
 * Custom subclass of {@link MySqlConnector} that introduces restart logic for the debezium route
 * when the connection to the OpenMRS database is lost and later re-established.
 */
public class FailureTolerantMySqlConnector extends MySqlConnector {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FailureTolerantMySqlConnector.class);
	
	private static ScheduledExecutorService executor;
	
	protected static final String EXECUTOR_NAME = "OpenMRS DB reconnect watchdog";
	
	protected static final int EXECUTOR_SHUTDOWN_TIMEOUT = 5000;
	
	@Override
	public void stop() {
		super.stop();
		
		if (Utils.isShuttingDown()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Debezium stopped due to application shutdown, skipping reconnect watchdog");
			}
			
			stopReconnectWatchDogExecutor();
			
			return;
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Suspending debezium route");
		}
		
		try {
			AppContext.getBean(CamelContext.class).getRouteController().suspendRoute(DEBEZIUM_ROUTE_ID);
		}
		catch (Exception e) {
			LOGGER.error("Failed to suspend debezium route", e);
			Utils.shutdown();
			return;
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("OpenMRS DB is unreachable, starting reconnect watchdog");
		}
		
		executor = createExecutor();
		OpenmrsDbReconnectWatchDog watchDog = AppContext.getBean(OpenmrsDbReconnectWatchDog.class);
		
		try {
			Long delay = AppContext.getBean(Environment.class).getProperty(PROP_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY,
			    Long.class, DEFAULT_OPENMRS_DB_RECONNECT_WATCHDOG_DELAY);
			
			executor.scheduleWithFixedDelay(watchDog, delay, delay, TimeUnit.MILLISECONDS);
		}
		catch (Exception e) {
			LOGGER.error("Failed to start OpenMRS DB disconnect watchdog", e);
			Utils.shutdown();
		}
	}
	
	/**
	 * Stops the executor for the OpenMRS DB reconnect watchdog thread
	 */
	protected static void stopReconnectWatchDogExecutor() {
		if (executor != null && !executor.isTerminated()) {
			Utils.shutdownExecutor(executor, EXECUTOR_NAME, EXECUTOR_SHUTDOWN_TIMEOUT);
		}
	}
	
	protected ScheduledExecutorService createExecutor() {
		return Executors.newSingleThreadScheduledExecutor();
	}
	
}
