package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.Constants.BEAN_OPENMRS_DS_HEALTH_INDICATOR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

/**
 * Monitors the OpenMRS database connection and starts the {@link OpenmrsDbReconnectHandler} upon a
 * successful reconnection
 */
@Component
public class OpenmrsDbReconnectWatchDog implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenmrsDbReconnectWatchDog.class);
	
	private OpenmrsDbReconnectHandler reconnectHandler;
	
	private HealthIndicator indicator;
	
	public OpenmrsDbReconnectWatchDog(OpenmrsDbReconnectHandler reconnectHandler,
	    @Qualifier(BEAN_OPENMRS_DS_HEALTH_INDICATOR) HealthIndicator indicator) {
		this.reconnectHandler = reconnectHandler;
		this.indicator = indicator;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("openmrs-db-reconnect-watchdog");
		
		if (indicator.health().getStatus() != Status.UP) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("OpenMRS DB is still unreachable");
			}
			
			return;
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("OpenMRS DB has become available");
		}
		
		startReconnectHandler();
	}
	
	protected void startReconnectHandler() {
		new Thread(reconnectHandler).start();
	}
	
}
