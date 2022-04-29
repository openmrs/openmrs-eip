package org.openmrs.eip.mysql.watcher;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.openmrs.eip.mysql.watcher.route.DebeziumRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers and calls the debezium route
 */
public class MySqlWatcherProducer extends DefaultProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(MySqlWatcherProducer.class);
	
	private static final String DEBEZIUM_FROM_URI = "debezium-mysql:extract?databaseServerId={{debezium.db.serverId}}"
	        + "&databaseServerName={{debezium.db.serverName}}&databaseHostname={{openmrs.db.host}}"
	        + "&databasePort={{openmrs.db.port}}&databaseUser={{debezium.db.user}}"
	        + "&databasePassword={{debezium.db.password}}&databaseWhitelist={{openmrs.db.name}}"
	        + "&offsetStorageFileName={{debezium.offsetFilename}}"
	        + "&databaseHistoryFileFilename={{debezium.historyFilename}}&tableWhitelist={{debezium.tablesToSync}}"
	        + "&offsetFlushIntervalMs=0&snapshotMode={{debezium.snapshotMode}}"
	        + "&snapshotFetchSize=1000&snapshotLockingMode={{debezium.snapshotLockingMode}}&includeSchemaChanges=false"
	        + "&maxBatchSize={{debezium.reader.maxBatchSize}}&offsetStorage={{"
	        + WatcherConstants.PROP_DBZM_OFFSET_STORAGE_CLASS + "}}&databaseHistory={{"
	        + WatcherConstants.PROP_DBZM_OFFSET_HISTORY_CLASS + "}}&offsetCommitTimeoutMs=15000";
	
	public MySqlWatcherProducer(Endpoint endpoint) {
		super(endpoint);
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("Registering debezium route");
		
		exchange.getContext().addRoutes(new DebeziumRoute(DEBEZIUM_FROM_URI, WatcherConstants.SHUTDOWN_HANDLER_REF));
	}
	
}
