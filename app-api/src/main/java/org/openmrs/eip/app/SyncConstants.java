package org.openmrs.eip.app;

public class SyncConstants {
	
	public static final String FOLDER_DIST = "distribution";
	
	public static final String FOLDER_ROUTES = "routes";
	
	public static final int MAX_COUNT = 200;
	
	public static final int WAIT_IN_SECONDS = 60;
	
	public static final int DEFAULT_SITE_PARALLEL_SIZE = 5;
	
	public static final int DEFAULT_MSG_PARALLEL_SIZE = 10;
	
	public static final int DEFAULT_CONN_POOL_SIZE = 50;
	
	public static final int DEFAULT_BATCH_SIZE = 100;
	
	public static final String DEFAULT_OPENMRS_POOL_NAME = "openmrs-ds-pool";
	
	public static final String DEFAULT_MGT_POOL_NAME = "mgt-ds-pool";
	
	public static final String PROP_SITE_PARALLEL_SIZE = "sites.sync.parallel.size";
	
	public static final String PROP_MSG_PARALLEL_SIZE = "events.sync.parallel.size";
	
	public static final String EX_APP_ID = "org.openmrs.eip.app-appId";
	
	public final static String SAVEPOINT_FILE = "snapshot_savepoint.properties";
	
	public final static String OPENMRS_DATASOURCE_NAME = "openmrsDataSource";
	
	public final static String MGT_DATASOURCE_NAME = "mngtDataSource";
	
	public final static String CUSTOM_PROP_SOURCE_BEAN_NAME = "customPropSource";
	
	public final static String MGT_ENTITY_MGR = "mngtEntityManager";
	
	public final static String MGT_TX_MGR = "mngtTransactionManager";
	
	public final static String EX_PROP_APP_ID = "org.openmrs.eip.app-appId";
	
	public static final String ROUTE_ID_SHUTDOWN = "shutdown-route";
	
	public static final String URI_SHUTDOWN = "direct:" + ROUTE_ID_SHUTDOWN;
	
}
