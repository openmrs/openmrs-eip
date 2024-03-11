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
	
	public static final String PROP_INITIAL_DELAY_SYNC = "sender-sync-msg-reader.initial.delay";
	
	public static final String PROP_DELAY_SYNC = "sender-sync-msg-reader.delay";
	
	public static final int DEFAULT_DELAY_PRUNER = 86400000;
	
	public static final String PROP_INITIAL_DELAY_RECONCILER = "reconcile.initial.delay";
	
	public static final String PROP_DELAY_RECONCILER = "reconcile.delay";
	
	public static final String PROP_INITIAL_DELAY_MSG_RECONCILER = "reconcile.msg.initial.delay";
	
	public static final String PROP_DELAY_MSG_RECONCILER = "reconcile.msg.delay";
	
	public static final String PROP_INITIAL_DELAY_TABLE_RECONCILER = "reconcile.table.initial.delay";
	
	public static final String PROP_DELAY_TABLE_RECONCILER = "reconcile.table.delay";
	
	public static final String SYNC_BATCH_PROP_SIZE = "batchSize";
	
	public static final String PROP_LARGE_MSG_SIZE = "jms.large.msg.size";
	
	public static final String RECONCILE_MSG_SEPARATOR = ",";
	
	public static final String PROP_MIN_BATCH_RECONCILE_SIZE = "reconcile.process.min.batch.size";
	
	public static final String PROP_MAX_BATCH_RECONCILE_SIZE = "reconcile.process.max.batch.size";
	
	public static final String PROP_RECONCILE_MSG_BATCH_SIZE = "reconcile.batch.size";
	
	public static final int RECONCILE_MSG_BATCH_SIZE = 1000;
	
	//By default, artemisMQ considers a message larger than 100KiB to be a large message.
	//We need to try and avoid sending large messages by compressing any message close to half the large size.
	//Our limit is set to half of that of artemis because artemis message encoding uses 2 bytes per character.
	public static final int DEFAULT_LARGE_MSG_SIZE = 45 * 1024 * 1024;
	
	public static final String JMS_HEADER_SITE = "eipSite";
	
	public static final String JMS_HEADER_TYPE = "eipMessageType";
	
	public static final String JMS_HEADER_MSG_ID = "eipMessageId";
	
}
