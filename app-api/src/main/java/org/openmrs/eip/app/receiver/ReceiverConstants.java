package org.openmrs.eip.app.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReceiverConstants {
	
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static final String PARENT_TASK_NAME = "site parent task";
	
	public static final String CHILD_TASK_NAME = "child task";
	
	public static final String BEAN_NAME_SITE_EXECUTOR = "siteExecutor";
	
	public static final int DEFAULT_TASK_BATCH_SIZE = 1000;
	
	public static final int DEFAULT_SITE_PARALLEL_SIZE = 5;
	
	public static final String PROP_MSG_DESTINATION = "message.destination";
	
	public static final String PROP_ACTIVEMQ_IN_ENDPOINT = "camel.input.endpoint";
	
	public static final String PROP_CAMEL_OUTPUT_ENDPOINT = "camel.output.endpoint";
	
	public static final String PROP_RECEIVER_ID = "db-sync.receiverId";
	
	public static final String PROP_SYNC_TASK_BATCH_SIZE = "sync.task.batch.size";
	
	public static final String PROP_SITE_TASK_INITIAL_DELAY = "site.task.initial.delay";
	
	public static final String PROP_SITE_TASK_DELAY = "site.task.delay";
	
	public static final String PROP_SITE_DISABLED_TASKS = "site.disabled.tasks";
	
	public static final String PROP_PRUNER_ENABLED = "archives.pruner.task.enabled";
	
	public static final String PROP_ARCHIVES_MAX_AGE_DAYS = "archives.pruner.max.age.days";
	
	public static final String PROP_INITIAL_DELAY_PRUNER = "archives.pruner.initial.delay";
	
	public static final String PROP_DELAY_PRUNER = "archives.pruner.delay";
	
	public static final String PROP_PRIORITIZE_DISABLED = "sync.prioritize.disabled";
	
	public static final String PROP_BACKLOG_THRESHOLD = "sync.prioritize.backlog.threshold.days";
	
	public static final String PROP_SYNC_TIME_PER_ITEM = "sync.prioritize.time.per.item";
	
	public static final String PROP_PRIORITIZE_THRESHOLD = "sync.prioritize.threshold";
	
	public static final String PROP_COUNT_CACHE_TTL = "sync.prioritize.count.cache.ttl";
	
	public static final String ROUTE_ID_MSG_PROCESSOR = "message-processor";
	
	public static final String URI_MSG_PROCESSOR = "direct:" + ROUTE_ID_MSG_PROCESSOR;
	
	public static final String ROUTE_ID_INBOUND_DB_SYNC = "inbound-db-sync";
	
	public static final String URI_INBOUND_DB_SYNC = "direct:" + ROUTE_ID_INBOUND_DB_SYNC;
	
	public static final String ERROR_HANDLER_REF = "inBoundErrorHandler";
	
	public static final String EX_PROP_PAYLOAD = "entity-payload";
	
	public static final String EX_PROP_MODEL_CLASS = "model-class";
	
	public static final String EX_PROP_ENTITY_ID = "entity-id";
	
	public static final String EX_PROP_SYNC_MESSAGE = "sync-message";
	
	public static final String EX_PROP_FAILED_ENTITIES = "failed-entities";
	
	public static final String EX_PROP_RETRY_ITEM_ID = "retry-item-id";
	
	public static final String EX_PROP_RETRY_ITEM = "retry-item";
	
	public static final String EX_PROP_METADATA = "sync-metadata";
	
	public static final String EX_PROP_IS_FILE = "is-file";
	
	public static final String EX_PROP_MSG_PROCESSED = "org.openmrs.eip.app.receiver.sync-msgProcessed";
	
	public static final String EX_PROP_MOVED_TO_CONFLICT_QUEUE = "org.openmrs.eip.app.receiver.sync-movedToConflictQueue";
	
	public static final String EX_PROP_MOVED_TO_ERROR_QUEUE = "org.openmrs.eip.app.receiver.sync-movedToErrorQueue";
	
	public static final String ROUTE_ID_REQUEST_PROCESSOR = "receiver-request-processor";
	
	public static final String URI_REQUEST_PROCESSOR = "direct:" + ROUTE_ID_REQUEST_PROCESSOR;
	
	public static final String ROUTE_ID_RETRY = "receiver-retry";
	
	public static final String URI_RETRY = "direct:" + ROUTE_ID_RETRY;
	
	public static final String ROUTE_ID_RECEIVER_MAIN = "receiver-main";
	
	public static final String ROUTE_ID_UPDATE_LAST_SYNC_DATE = "update-site-last-sync-date";
	
	public static final String URI_UPDATE_LAST_SYNC_DATE = "seda:" + ROUTE_ID_UPDATE_LAST_SYNC_DATE;
	
	public static final String ROUTE_ID_COMPLEX_OBS_SYNC = "inbound-complex-obs-sync";
	
	public static final String URI_COMPLEX_OBS_SYNC = "direct:" + ROUTE_ID_COMPLEX_OBS_SYNC;
	
	public static final String ROUTE_ID_UPDATE_SEARCH_INDEX = "receiver-update-search-index";
	
	public static final String URI_UPDATE_SEARCH_INDEX = "direct:" + ROUTE_ID_UPDATE_SEARCH_INDEX;
	
	public static final String ROUTE_ID_CLEAR_CACHE = "receiver-clear-db-cache";
	
	public static final String URI_CLEAR_CACHE = "direct:" + ROUTE_ID_CLEAR_CACHE;
	
	public static final String ROUTE_ID_DBSYNC = "inbound-db-sync";
	
	public static final String URI_DBSYNC = "direct:" + ROUTE_ID_DBSYNC;
	
	public static final String ROUTE_ID_ERROR_HANDLER = "inbound-error-handler";
	
	public static final String URI_ERROR_HANDLER = "direct:" + ROUTE_ID_ERROR_HANDLER;
	
}
