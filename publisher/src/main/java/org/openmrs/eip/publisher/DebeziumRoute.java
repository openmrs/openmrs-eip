package org.openmrs.eip.publisher;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures, invokes the {@link org.apache.camel.component.debezium.DebeziumMySqlComponent} and
 * registers a listener to be notified whenever a DB event is received from the debezium engine. It
 * also registers a {@link DebeziumMessageProcessor} instance to pre-process every event before
 * notifying the main db event listener.
 */
public class DebeziumRoute extends RouteBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(DebeziumRoute.class);
	
	private PublisherEndpoint endpoint;
	
	public DebeziumRoute(PublisherEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	@Override
	public void configure() {
		logger.info("Starting debezium...");
		
		RouteDefinition routeDef = from(
		    "debezium-mysql:extract?databaseServerId={{debezium.db.serverId}}&databaseServerName={{debezium.db.serverName}}&databaseHostname={{openmrs.db.host}}&databasePort={{openmrs.db.port}}&databaseUser={{debezium.db.user}}&databasePassword={{debezium.db.password}}&databaseWhitelist={{openmrs.db.name}}&offsetStorageFileName={{debezium.offsetFilename}}&databaseHistoryFileFilename={{debezium.historyFilename}}&tableWhitelist={{debezium.tablesToSync}}&offsetFlushIntervalMs=0&snapshotMode=initial&snapshotFetchSize=1000&snapshotLockingMode=extended&includeSchemaChanges=false")
		            .process(new DebeziumMessageProcessor()).to("direct:db-event-listener");
		
		logger.info("Setting debezium route handler to: " + OpenmrsEipConstants.ERROR_HANDLER_REF);
		
		routeDef.setErrorHandlerRef(OpenmrsEipConstants.ERROR_HANDLER_REF);
	}
	
}
