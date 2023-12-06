package org.openmrs.eip.mysql.watcher;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.openmrs.eip.mysql.watcher.route.DebeziumRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers and calls the debezium route See
 * https://camel.apache.org/components/3.20.x/debezium-mysql-component.html for properties
 */
public class MySqlWatcherProducer extends DefaultProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(MySqlWatcherProducer.class);
	
	private static final String DEBEZIUM_FROM_URI = "debezium-mysql:extract?additionalProperties.connector.class="
	        + FailureTolerantMySqlConnector.class.getName() + "&databaseServerId={{debezium.db.serverId}}"
	        + "&databaseHostname={{openmrs.db.host}}" + "&databasePort={{openmrs.db.port}}&databaseUser={{debezium.db.user}}"
	        + "&databasePassword={{debezium.db.password}}&databaseIncludeList={{openmrs.db.name}}"
	        + "&offsetStorageFileName={{debezium.offsetFilename}}" + "&topicPrefix={{debezium.db.serverName}}"
	        + "&schemaHistoryInternalFileFilename={{debezium.historyFilename}}&tableIncludeList={{debezium.tablesToSync}}"
	        + "&offsetFlushIntervalMs=0&snapshotMode={{debezium.snapshotMode}}"
	        + "&snapshotFetchSize=1000&snapshotLockingMode={{debezium.snapshotLockingMode}}&includeSchemaChanges=false"
	        + "&maxBatchSize={{debezium.reader.maxBatchSize}}&offsetStorage={{"
	        + WatcherConstants.PROP_DBZM_OFFSET_STORAGE_CLASS + "}}&schemaHistoryInternal={{"
	        + WatcherConstants.PROP_DBZM_OFFSET_HISTORY_CLASS + "}}&offsetCommitTimeoutMs=15000"
	        + "{{debezium.extraParameters:}}";
	
	public MySqlWatcherProducer(Endpoint endpoint) {
		super(endpoint);
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("Registering debezium route");
		
		exchange.getContext().addRoutes(new DebeziumRoute(DEBEZIUM_FROM_URI, WatcherConstants.SHUTDOWN_HANDLER_REF));
	}
	
}
