package org.openmrs.eip.app;

import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.springframework.stereotype.Component;

/**
 * Watches for specific camel events and responds to them accordingly
 */
@Component
public class AppCamelListener extends EventNotifierSupport {
	
	@Override
	public void notify(CamelEvent event) throws Exception {
		if (event instanceof CamelEvent.CamelContextStoppingEvent) {
			AppUtils.setAppContextStopping();
		}
	}
	
}
