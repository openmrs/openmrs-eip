package org.openmrs.eip.app.receiver;

public class ReceiverConstants {
	
	public static final String ROUTE_ID_MSG_PROCESSOR = "message-processor";
	
	public static final String URI_MSG_PROCESSOR = "direct:" + ROUTE_ID_MSG_PROCESSOR;
	
	public static final String ROUTE_ID_INBOUND_DB_SYNC = "inbound-db-sync";
	
	public static final String URI_INBOUND_DB_SYNC = "direct:" + ROUTE_ID_INBOUND_DB_SYNC;
	
	public static final String ERROR_HANDLER_REF = "inBoundErrorHandler";
	
}
