package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.Constants.BEAN_OPENMRS_DS_HEALTH_INDICATOR;

import org.openmrs.eip.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

/**
 * Monitors the OpenMRS database connection and starts the {@link OpenmrsDbReconnectHandler} upon a
 * successful reconnection
 */
public class OpenmrsDbReconnectWatchDog implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenmrsDbReconnectWatchDog.class);
	
	private OpenmrsDbReconnectHandler reconnectHandler;
	
	public OpenmrsDbReconnectWatchDog(OpenmrsDbReconnectHandler reconnectHandler) {
		this.reconnectHandler = reconnectHandler;
	}
	
	@Override
	public void run() {
		Thread.currentThread().setName("openmrs-db-reconnect-watchdog");
		
		HealthIndicator indicator = AppContext.getBean(BEAN_OPENMRS_DS_HEALTH_INDICATOR);
		
		if (indicator.health().getStatus() != Status.UP) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("OpenMRS DB is still unreachable");
			}
			
			return;
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("OpenMRS DB has become available");
		}
		
		new Thread(reconnectHandler).start();
	}
	
}
