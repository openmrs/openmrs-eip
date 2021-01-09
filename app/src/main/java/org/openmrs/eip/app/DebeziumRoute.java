package org.openmrs.eip.app;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures, invokes the {@link org.apache.camel.component.debezium.DebeziumMySqlComponent} and registers a
 * listener to be notified whenever a DB event is received from the debezium engine.
 * It also registers a {@link PublisherProcessor} instance to pre-process every event before notifying the listener.
 */
public class DebeziumRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DebeziumRoute.class);

    private String listener;

    public DebeziumRoute(String listener) {
        this.listener = listener;
    }

    @Override
    public void configure() {
        logger.info("Starting debezium...");

        from("debezium-mysql:extract?databaseServerId={{debezium.db.serverId}}&databaseServerName={{debezium.db.serverName}}&databaseHostname={{openmrs.db.host}}&databasePort={{openmrs.db.port}}&databaseUser={{debezium.db.user}}&databasePassword={{debezium.db.password}}&databaseWhitelist={{openmrs.db.name}}&offsetStorageFileName={{debezium.offsetFilename}}&databaseHistoryFileFilename={{debezium.historyFilename}}&tableWhitelist={{debezium.tablesToSync}}&offsetFlushIntervalMs=0&snapshotMode=initial&snapshotFetchSize=1000&snapshotLockingMode=extended&includeSchemaChanges=false").
                process(new PublisherProcessor()).
                to(listener); //TODO support other listener kinds e.g. a spring bean

    }

}
