package org.openmrs.eip.mysql.watcher.route;

import org.apache.camel.builder.RouteBuilder;
import org.openmrs.eip.Utils;
import org.openmrs.eip.mysql.watcher.CustomFileOffsetBackingStore;
import org.springframework.stereotype.Component;

/**
 * Route that is invoked when the watcher application is shutdown. - Disables the
 * CustomFileOffsetBackingStore - Sends a notification to the configured recipients
 */
@Component
public class WatcherShutdownRoute extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
		from("direct:watcher-shutdown").routeId("watcher-shutdown")
		        .process(exchange -> CustomFileOffsetBackingStore.disable())
		        
		        // TODO: Add notification logic here
		        .log("An error occurred, cause: ${exception.message}").log("Shutting down the application...")
		        .process(exchange -> Utils.shutdown()).end();
	}
}
