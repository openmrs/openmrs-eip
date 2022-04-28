package org.openmrs.eip.mysql.watcher;

public class WatcherConstants {
	
	public static final String PKG_NAME = WatcherConstants.class.getPackage().getName();
	
	public static final String DEBEZIUM_FIELD_TABLE = "table";
	
	public static final String DEBEZIUM_FIELD_SNAPSHOT = "snapshot";
	
	public static final String FIELD_UUID = "uuid";
	
	public static final String PROP_EVENT = "event";
	
	public static final String DEBEZIUM_ROUTE_ID = "debezium-route";
	
	public static final String SHUTDOWN_HANDLER_REF = "watcherShutdownErrorHandler";
	
	public static final String ERROR_HANDLER_REF = "watcherErrorHandler";
	
	public static final String DBZM_MSG_PROCESSOR = "debezium-msg-processor";
	
	public static final String ID_SETTING_PROCESSOR = "id-setting-event-processor";
	
	public static final String PROP_EVENT_DESTINATIONS = "db-event.destinations";
	
	public static final String PROP_URI_EVENT_PROCESSOR = "watcher.uri.event.processor";
	
	public static final String PROP_DBZM_OFFSET_STORAGE_CLASS = "debezium.offsetStorage";
	
	public static final String PROP_DBZM_OFFSET_HISTORY_CLASS = "debezium.databaseHistory";
	
	public static final String PROP_URI_ERROR_HANDLER = "watcher.uri.error.handler";
	
	public static final String URI_EVENT_PROCESSOR = "direct:db-event-processor";
	
	public static final String URI_ERROR_HANDLER = "direct:watcher-error-handler";
	
	public static final String PROP_SOURCE_NAME = "watcherPropSource";
	
	public static final String PROP_IGNORE_PREV_ORDER_IN_ERROR_QUEUE = "ignore.previous.order.in.error.queue";
	
	public static final String EVENT_AUT_COLUMNS_FILTER_BEAN_NAME = "auditableColumnsEventFilter";
	
	public static final String EX_PROP_SKIP = PKG_NAME + ".skip";
	
	public static final String COLUMN_CHANGED_BY = "changed_by";
	
	public static final String COLUMN_DATE_CHANGED = "date_changed";
	
	public static final String PROP_AUDIT_FILTER_TABLES = "filter.auditable.tables";
	
}
