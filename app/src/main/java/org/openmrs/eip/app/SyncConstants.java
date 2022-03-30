package org.openmrs.eip.app;

public class SyncConstants {
	
	public static final int MAX_COUNT = 1000;
	
	public static final int WAIT_IN_SECONDS = 15;
	
	public static final int DEFAULT_SYNC_THREAD_SIZE = 10;
	
	public static final String PROP_SYNC_THREAD_SIZE = "sync.threads.size";

    public static final String ROUTE_URI_SYNC_PROCESSOR = "direct:message-processor";
	
}
