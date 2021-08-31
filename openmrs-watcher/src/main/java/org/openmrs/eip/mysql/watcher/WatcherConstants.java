package org.openmrs.eip.mysql.watcher;

public class WatcherConstants {
	
	public static final String DEBEZIUM_FIELD_TABLE = "table";
	
	public static final String DEBEZIUM_FIELD_SNAPSHOT = "snapshot";
	
	public static final String FIELD_UUID = "uuid";
	
	public static final String PROP_EVENT = "event";
	
	public static final String DEBEZIUM_ROUTE_ID = "debezium-route";
	
	public static final String SHUTDOWN_HANDLER_REF = "shutdownErrorHandler";
	
	public static final String ERROR_HANDLER_REF = "watcherErrorHandler";
	
	public static final String DBZM_MSG_PROCESSOR = "debezium-msg-processor";
	
	public static final String ID_SETTING_PROCESSOR = "id-setting-event-processor";
	
	public static final String PROP_EVENT_DESTINATIONS = "db-event.destinations";
	
	public static final String PROP_URI_EVENT_PROCESSOR = "watcher.uri.event.processor";
	
	public static final String PROP_URI_ERROR_HANDLER = "watcher.uri.error.handler";
	
	public static final String URI_EVENT_PROCESSOR = "direct:db-event-processor";
	
	public static final String URI_ERROR_HANDLER = "direct:watcher-error-handler";
	
	public static final String PROP_SOURCE_NAME = "watcherPropSource";
	
}
