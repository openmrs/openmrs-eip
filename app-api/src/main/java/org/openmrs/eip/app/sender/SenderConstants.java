package org.openmrs.eip.app.sender;

public class SenderConstants {
	
	public static final String ACTIVEMQ_IN_ENDPOINT = "activemq:openmrs.sync.{{db-sync.senderId}}?connectionFactory=activeMqConnFactory&acknowledgementModeName=CLIENT_ACKNOWLEDGE&messageListenerContainerFactory=customMessageListenerContainerFactory&asyncStartListener=true";
	
	public static final String BEAN_NAME_SCHEDULED_EXECUTOR = "scheduledExecutor";
	
	public static final String PROP_ACTIVEMQ_IN_ENDPOINT = "db-sync.sender.activemq.in";
	
	public static final String PROP_ACTIVEMQ_ENDPOINT = "camel.output.endpoint";
	
	public static final String PROP_SENDER_ID = "db-sync.senderId";
	
	public static final String PROP_JMS_SEND_BATCH_SIZE = "jms.send.batch.size";
	
	public static final String PROP_DBZM_SERVER_ID = "debezium.db.serverId";
	
	public static final String PROP_DBZM_DB_USER = "debezium.db.user";
	
	public static final String PROP_DBZM_DB_PASSWORD = "debezium.db.password";
	
	public static final String PROP_DBZM_OFFSET_FILENAME = "debezium.offsetFilename";
	
	public static final String PROP_BINLOG_PURGER_ENABLED = "binlog.purger.task.enabled";
	
	public static final String PROP_BINLOG_MAX_KEEP_COUNT = "binlog.files.processed.keep.max";
	
	public static final String PROP_INITIAL_DELAY_BINLOG_PURGER = "binlog.purger.initial.delay";
	
	public static final String PROP_DELAY_BINLOG_PURGER = "binlog.purger.delay";
	
	public static final String EX_PROP_EVENT = "event";
	
	public static final String EX_PROP_DBZM_EVENT = "dbzmEvent";
	
	public static final String EX_PROP_IS_SUBCLASS = "is-subclass";
	
	public static final String EX_PROP_RETRY_ITEM_ID = "retry-item-id";
	
	public static final String EX_PROP_RETRY_ITEM = "retry-item";
	
	public static final String EX_PROP_FAILED_ENTITIES = "failed-entities";
	
	public static final String ROUTE_ID_DB_EVENT_PROCESSOR = "db-event-processor";
	
	public static final String URI_DB_EVENT_PROCESSOR = "direct:" + ROUTE_ID_DB_EVENT_PROCESSOR;
	
	public static final String ROUTE_ID_DBZM_EVENT_PROCESSOR = "debezium-event-processor";
	
	public static final String URI_DBZM_EVENT_PROCESSOR = "direct:" + ROUTE_ID_DBZM_EVENT_PROCESSOR;
	
	public static final String ROUTE_ID_SYNC_MSG_READER = "sender-sync-msg-reader";
	
	public static final String URI_SYNC_MSG_READER = "direct:" + ROUTE_ID_SYNC_MSG_READER;
	
	public static final String ROUTE_ID_DBSYNC = "out-bound-db-sync";
	
	public static final String URI_DBSYNC = "direct:" + ROUTE_ID_DBSYNC;
	
	public static final String ROUTE_ID_DBZM_EVENT_READER = "debezium-event-reader";
	
	public static final String URI_DBZM_EVENT_READER = "direct:debezium-event-reader";
	
	public static final String ROUTE_ID_RETRY = "sender-retry";
	
	public static final String URI_RETRY = "direct:" + ROUTE_ID_RETRY;
	
	public static final String ROUTE_ID_ERROR_HANDLER = "outbound-error-handler";
	
	public static final String URI_ERROR_HANDLER = "direct:" + ROUTE_ID_ERROR_HANDLER;
	
	public static final String ROUTE_ID_ACTIVEMQ_CONSUMER = "sender-activemq-consumer";
	
	public static final String ROUTE_ID_REQUEST_PROCESSOR = "sender-request-processor";
	
	public static final String URI_REQUEST_PROCESSOR = "direct:" + ROUTE_ID_REQUEST_PROCESSOR;
	
	public static final String ROUTE_ID_RESPONSE_PROCESSOR = "sync-response-processor";
	
	public static final String URI_RESPONSE_PROCESSOR = "direct:" + ROUTE_ID_RESPONSE_PROCESSOR;
	
	public static final String ROUTE_ID_RESPONSE_READER = "sync-response-reader";
	
	public static final String URI_RESPONSE_READER = "direct:" + ROUTE_ID_RESPONSE_READER;
	
	public static final String ERROR_HANDLER_REF = "outBoundErrorHandler";
	
	public static final String MASK = "#####";
	
	public static final String OFFSET_PROP_FILE = "file";
	
	public static final String OFFSET_PROP_POSITION = "pos";
	
	public static final String OFFSET_PROP_ROW = "row";
	
	public static final String OFFSET_PROP_EVENT = "event";
	
}
