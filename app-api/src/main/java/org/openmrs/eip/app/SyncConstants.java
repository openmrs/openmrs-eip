package org.openmrs.eip.app;

public class SyncConstants {
	
	public static final String DBSYNC_PROP_FILE = "dbsync.properties";
	
	public static final String DBSYNC_PROP_VERSION = "version";
	
	public static final String DBSYNC_PROP_BUILD_NUMBER = "build.number";
	
	public static final String FOLDER_DIST = "distribution";
	
	public static final String FOLDER_ROUTES = "routes";
	
	public static final String BEAN_NAME_SYNC_EXECUTOR = "syncExecutor";
	
	public static final int EXECUTOR_SHUTDOWN_TIMEOUT = 15;
	
	public static final int DEFAULT_CONN_POOL_SIZE = 50;
	
	public static final int THREAD_THRESHOLD_MULTIPLIER = 2;
	
	public static final String DEFAULT_OPENMRS_POOL_NAME = "openmrs-ds-pool";
	
	public static final String DEFAULT_MGT_POOL_NAME = "mgt-ds-pool";
	
	public static final String PROP_SITE_PARALLEL_SIZE = "sites.sync.parallel.size";
	
	public static final String PROP_THREAD_NUMBER = "parallel.processing.thread.number";
	
	public final static String SAVEPOINT_FILE = "snapshot_savepoint.properties";
	
	public final static String OPENMRS_DATASOURCE_NAME = "openmrsDataSource";
	
	public final static String MGT_DATASOURCE_NAME = "mngtDataSource";
	
	public final static String MGT_ENTITY_MGR = "mngtEntityManager";
	
	public final static String MGT_TX_MGR = "mngtTransactionManager";
	
	public final static String OPENMRS_TX_MGR = "openmrsTransactionManager";
	
	public final static String CHAINED_TX_MGR = "chainedTxManager";
	
	public static final String ROUTE_ID_SHUTDOWN = "shutdown-route";
	
	public static final String URI_SHUTDOWN = "direct:" + ROUTE_ID_SHUTDOWN;
	
	public static final String PROP_PRUNER_ENABLED = "archives.pruner.task.enabled";
	
	public static final String PROP_ARCHIVES_MAX_AGE_DAYS = "archives.pruner.max.age.days";
	
	public static final String PROP_INITIAL_DELAY_PRUNER = "archives.pruner.initial.delay";
	
	public static final String PROP_DELAY_PRUNER = "archives.pruner.delay";
	
	public static final int DEFAULT_DELAY_PRUNER = 86400000;
	
}
