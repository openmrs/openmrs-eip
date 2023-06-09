package org.openmrs.eip.mysql.watcher;

import static org.apache.camel.LoggingLevel.DEBUG;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DBZM_MSG_PROCESSOR;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.ID_SETTING_PROCESSOR;

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
	
	public DebeziumRoute() {
	}
	
	@Override
	public void configure() {
		logger.info("Starting debezium...");
		
		RouteDefinition routeDef = from(
		    "debezium-mysql:extract?databaseServerId={{debezium.db.serverId}}&databaseServerName={{debezium.db.serverName}}&databaseHostname={{openmrs.db.host}}&databasePort={{openmrs.db.port}}&databaseUser={{debezium.db.user}}&databasePassword={{debezium.db.password}}&databaseWhitelist={{openmrs.db.name}}&offsetStorageFileName={{debezium.offsetFilename}}&databaseHistoryFileFilename={{debezium.historyFilename}}&tableWhitelist={{debezium.tablesToSync}}&offsetFlushIntervalMs=0&snapshotMode={{debezium.snapshotMode}}&snapshotFetchSize=1000&snapshotLockingMode={{debezium.snapshotLockingMode}}&includeSchemaChanges=false&maxBatchSize={{debezium.reader.maxBatchSize}}&offsetStorage={{"
		            + WatcherConstants.PROP_DBZM_OFFSET_STORAGE_CLASS + "}}&databaseHistory={{"
		            + WatcherConstants.PROP_DBZM_OFFSET_HISTORY_CLASS + "}}&offsetCommitTimeoutMs=15000"+"{{debezium.extraParameters:}}")
		                    .routeId(DEBEZIUM_ROUTE_ID);
		
		logger.info("Setting debezium route handler to: " + WatcherConstants.SHUTDOWN_HANDLER_REF);
		
		routeDef.setErrorHandlerRef(WatcherConstants.SHUTDOWN_HANDLER_REF);
		
		routeDef.choice()
		        
		        .when(exchange -> !CustomFileOffsetBackingStore.isDisabled())
		        
		        .process(DBZM_MSG_PROCESSOR)
		        
		        .process(ID_SETTING_PROCESSOR)
		        
		        .to("direct:debezium-event-listener")
		        
		        .otherwise()
		        
		        .log(DEBUG, "Deferring DB event because an error was encountered while processing a previous one")
		        
		        .end();
	}
	
}
