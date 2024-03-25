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
	
	public static final String ERROR_HANDLER_REF = "inBoundErrorHandler";
	
	public static final String ROUTE_ID_REQUEST_PROCESSOR = "receiver-request-processor";
	
	public static final String URI_REQUEST_PROCESSOR = "direct:" + ROUTE_ID_REQUEST_PROCESSOR;
	
	public static final String FIELD_VOIDED = "voided";
	
	public static final String FIELD_RETIRED = "retired";
	
	public static final Set<String> MERGE_EXCLUDE_FIELDS = unmodifiableSet(
	    asList("changedByUuid", "dateChanged", "patientChangedByUuid", "patientDateChanged", "voidedByUuid", "dateVoided",
	        "voidReason", "patientVoidedByUuid", "patientDateVoided", "patientVoidReason", "retiredByUuid", "dateRetired",
	        "retireReason").stream().collect(Collectors.toSet()));
	
}
