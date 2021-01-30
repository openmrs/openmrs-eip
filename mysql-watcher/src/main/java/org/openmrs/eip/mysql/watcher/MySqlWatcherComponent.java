package org.openmrs.eip.mysql.watcher;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("openmrs-eip-mysql-watcher")
public class MySqlWatcherComponent extends DefaultComponent {
	
	private static final Logger logger = LoggerFactory.getLogger(MySqlWatcherComponent.class);
	
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
		logger.info("Creating watcher endpoint with parameters: " + parameters);
		return new MySqlWatcherEndpoint(uri, this);
	}
	
}
