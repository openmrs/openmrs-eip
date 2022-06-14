package org.openmrs.eip.app.sender;

public class SenderConstants {
	
	public static final String EX_PROP_EVENT = "event";
	
	public static final String EX_PROP_IS_SUBCLASS = "is-subclass";
	
	public static final String EX_PROP_ROUTE_RETRY_COUNT_MAP = "route-retry-count-map";
	
	public static final String ROUTE_ID_DB_EVENT_PROCESSOR_ROUTE = "db-event-processor";
	
	public static final String URI_DB_EVENT_PROCESSOR = "direct:" + ROUTE_ID_DB_EVENT_PROCESSOR_ROUTE;
	
	public static final String ROUTE_ID_DBZM_EVENT_PROCESSOR = "debezium-event-processor";
	
	public static final String URI_DBZM_EVENT_PROCESSOR = "direct:" + ROUTE_ID_DBZM_EVENT_PROCESSOR;
	
}
