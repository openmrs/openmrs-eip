package org.openmrs.eip.app.receiver;

public class ReceiverConstants {
	
	public static final String ROUTE_ID_MSG_PROCESSOR = "message-processor";
	
	public static final String URI_MSG_PROCESSOR = "direct:" + ROUTE_ID_MSG_PROCESSOR;
	
	public static final String ROUTE_ID_INBOUND_DB_SYNC = "inbound-db-sync";
	
	public static final String URI_INBOUND_DB_SYNC = "direct:" + ROUTE_ID_INBOUND_DB_SYNC;
	
	public static final String ERROR_HANDLER_REF = "inBoundErrorHandler";
	
	public static final String EX_PROP_PAYLOAD = "entity-payload";
	
	public static final String EX_PROP_MODEL_CLASS = "model-class";
	
	public static final String EX_PROP_ENTITY_ID = "entity-id";
	
	public static final String EX_PROP_SITE = "site";
	
	public static final String PROP_CAMEL_OUTPUT_ENDPOINT = "camel.output.endpoint";
	
	public static final String ROUTE_ID_REQUEST_PROCESSOR = "receiver-request-processor";
	
	public static final String URI_REQUEST_PROCESSOR = "direct:" + ROUTE_ID_REQUEST_PROCESSOR;
}
