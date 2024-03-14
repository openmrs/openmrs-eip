package org.openmrs.eip.app.receiver;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ReceiverConstants {
	
	public static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static final String PARENT_TASK_NAME = "site parent task";
	
	public static final String CHILD_TASK_NAME = "child task";
	
	public static final String BEAN_NAME_SITE_EXECUTOR = "siteExecutor";
	
	public static final int DEFAULT_TASK_BATCH_SIZE = 1000;
	
	public static final int DEFAULT_SITE_PARALLEL_SIZE = 5;
	
	public static final String PROP_MSG_DESTINATION = "message.destination";
	
	public static final String PROP_SYNC_QUEUE = "sync.queue.name";
	
	public static final String PROP_CAMEL_OUTPUT_ENDPOINT = "camel.output.endpoint";
	
	public static final String PROP_RECEIVER_ID = "db-sync.receiverId";
	
	public static final String PROP_SYNC_TASK_BATCH_SIZE = "sync.task.batch.size";
	
	public static final String PROP_INITIAL_DELAY_JMS_MSG_TASK = "jms.msg.task.initial.delay";
	
	public static final String PROP_DELAY_JMS_MSG_TASK = "jms.msg.task.delay";
	
	public static final String PROP_SITE_TASK_INITIAL_DELAY = "site.task.initial.delay";
	
	public static final String PROP_SITE_TASK_DELAY = "site.task.delay";
	
	public static final String PROP_SITE_DISABLED_TASKS = "site.disabled.tasks";
	
	public static final String PROP_PRIORITIZE_DISABLED = "sync.prioritize.disabled";
	
	public static final String PROP_BACKLOG_THRESHOLD = "sync.prioritize.backlog.threshold.days";
	
	public static final String PROP_SYNC_TIME_PER_ITEM = "sync.prioritize.time.per.item";
	
	public static final String PROP_PRIORITIZE_THRESHOLD = "sync.prioritize.threshold";
	
	public static final String PROP_COUNT_CACHE_TTL = "sync.prioritize.count.cache.ttl";
	
	public static final String ROUTE_ID_MSG_PROCESSOR = "message-processor";
	
	public static final String URI_MSG_PROCESSOR = "direct:" + ROUTE_ID_MSG_PROCESSOR;
	
	public static final String ROUTE_ID_INBOUND_DB_SYNC = "inbound-db-sync";
	
	public static final String URI_INBOUND_DB_SYNC = "direct:" + ROUTE_ID_INBOUND_DB_SYNC;
	
	public static final String ROUTE_ID_ERROR_HANDLER = "inbound-error-handler";
	
	public static final String URI_ERROR_HANDLER = "direct:" + ROUTE_ID_ERROR_HANDLER;
	
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
	
	public static final String EX_PROP_FOUND_CONFLICT = ROUTE_ID_INBOUND_DB_SYNC + "-foundConflict";
	
	public static final String EX_PROP_ERR_TYPE = ROUTE_ID_ERROR_HANDLER + "-errType";
	
	public static final String EX_PROP_ERR_MSG = ROUTE_ID_ERROR_HANDLER + "-errMsg";
	
	public static final String EX_PROP_IS_CONFLICT = "org.openmrs.eip.app.receiver.isConflictSync";
	
	public static final String ROUTE_ID_REQUEST_PROCESSOR = "receiver-request-processor";
	
	public static final String URI_REQUEST_PROCESSOR = "direct:" + ROUTE_ID_REQUEST_PROCESSOR;
	
	public static final String ROUTE_ID_RETRY = "receiver-retry";
	
	public static final String URI_RETRY = "direct:" + ROUTE_ID_RETRY;
	
	public static final String ROUTE_ID_DBSYNC = "inbound-db-sync";
	
	public static final String URI_DBSYNC = "direct:" + ROUTE_ID_DBSYNC;
	
	public static final String FIELD_VOIDED = "voided";
	
	public static final String FIELD_RETIRED = "retired";
	
	public static final Set<String> MERGE_EXCLUDE_FIELDS = unmodifiableSet(
	    asList("changedByUuid", "dateChanged", "patientChangedByUuid", "patientDateChanged", "voidedByUuid", "dateVoided",
	        "voidReason", "patientVoidedByUuid", "patientDateVoided", "patientVoidReason", "retiredByUuid", "dateRetired",
	        "retireReason").stream().collect(Collectors.toSet()));
	
}
