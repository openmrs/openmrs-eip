package org.openmrs.eip.mysql.watcher;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(firstVersion = "1.0.0", scheme = "openmrs-eip-mysql-watcher", title = "OpenMRS EIP MySql Watcher", syntax = "openmrs-eip-mysql-watcher:name", label = "openmrs,eip,watcher", producerOnly = true)
public class MySqlWatcherEndpoint extends DefaultEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(MySqlWatcherEndpoint.class);
	
	public MySqlWatcherEndpoint(String endpointUri, Component component) {
		this.setEndpointUri(endpointUri);
		this.setComponent(component);
	}
	
	@Override
	public Producer createProducer() {
		logger.info("Creating watcher producer");
		return new MySqlWatcherProducer(this);
	}
	
	@Override
	public Consumer createConsumer(Processor processor) {
		throw new UnsupportedOperationException();
	}
	
}
