package org.openmrs.eip.mysql.watcher;

import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.CamelContextStoppingEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.openmrs.eip.Utils;
import org.springframework.stereotype.Component;

/**
 * Applies graceful shutdown logic when the camel context is stopped
 */
@Component
public class ShutdownListener extends EventNotifierSupport {
	
	@Override
	public void notify(CamelEvent event) throws Exception {
		if (event instanceof CamelContextStoppingEvent) {
			Utils.setShuttingDown();
		}
	}
	
}
