package org.openmrs.eip.web.receiver;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.web.Dashboard;
import org.openmrs.eip.web.contoller.DashboardGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.RECEIVER)
public class ReceiverDashboardGenerator implements DashboardGenerator {
	
	private static final String SYNC_ENTITY_NAME = SyncMessage.class.getSimpleName();
	
	private static final String CONFLICT_ENTITY_NAME = ConflictQueueItem.class.getSimpleName();
	
	private static final String ERROR_ENTITY_NAME = ReceiverRetryQueueItem.class.getSimpleName();
	
	protected static final String KEY_TOTAL_COUNT = "totalCount";
	
	protected static final String KEY_ENTITY_STATS = "entityStatsMap";
	
	protected static final String KEY_SYNC_MSGS = "syncMsgs";
	
	protected static final String KEY_ERRORS = "errors";
	
	protected static final String KEY_CONFLICTS = "conflicts";
	
	protected ProducerTemplate producerTemplate;
	
	@Autowired
	public ReceiverDashboardGenerator(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}
	
	/**
	 * @see DashboardGenerator#generate()
	 */
	@Override
	public Dashboard generate() {
		final AtomicInteger totalSyncMsgCount = new AtomicInteger();
		final Map<String, Map> syncMsgsEntityStatsMap = new ConcurrentHashMap();
		final AtomicInteger totalErrorCount = new AtomicInteger();
		final Map<String, Map> errorEntityStatsMap = new ConcurrentHashMap();
		final AtomicInteger totalConflictCount = new AtomicInteger();
		final Map<String, Map> conflictEntityStatsMap = new ConcurrentHashMap();
		
		AppUtils.getClassAndSimpleNameMap().keySet().parallelStream().forEach(clazz -> {
			Arrays.stream(SyncOperation.values()).parallel().forEach(op -> {
				Integer msgCount = getCount(SYNC_ENTITY_NAME, clazz, op);
				totalSyncMsgCount.addAndGet(msgCount);
				
				if (msgCount > 0) {
					synchronized (this) {
						if (syncMsgsEntityStatsMap.get(clazz) == null) {
							syncMsgsEntityStatsMap.put(clazz, new ConcurrentHashMap());
						}
					}
					
					syncMsgsEntityStatsMap.get(clazz).put(op, msgCount);
				}
				
				Integer errorCount = getCount(ERROR_ENTITY_NAME, clazz, op);
				totalErrorCount.addAndGet(errorCount);
				
				if (errorCount > 0) {
					synchronized (this) {
						if (errorEntityStatsMap.get(clazz) == null) {
							errorEntityStatsMap.put(clazz, new ConcurrentHashMap());
						}
					}
					
					errorEntityStatsMap.get(clazz).put(op, errorCount);
				}
				
				Integer conflictCount = getCount(CONFLICT_ENTITY_NAME, clazz, op);
				totalConflictCount.addAndGet(conflictCount);
				
				if (conflictCount > 0) {
					synchronized (this) {
						if (conflictEntityStatsMap.get(clazz) == null) {
							conflictEntityStatsMap.put(clazz, new ConcurrentHashMap());
						}
					}
					
					conflictEntityStatsMap.get(clazz).put(op, conflictCount);
				}
				
			});
		});
		
		Map<String, Object> syncMsgs = new ConcurrentHashMap();
		syncMsgs.put(KEY_TOTAL_COUNT, totalSyncMsgCount);
		syncMsgs.put(KEY_ENTITY_STATS, syncMsgsEntityStatsMap);
		
		Map<String, Object> errors = new ConcurrentHashMap();
		errors.put(KEY_TOTAL_COUNT, totalErrorCount);
		errors.put(KEY_ENTITY_STATS, errorEntityStatsMap);
		
		Map<String, Object> conflicts = new ConcurrentHashMap();
		conflicts.put(KEY_TOTAL_COUNT, totalConflictCount);
		conflicts.put(KEY_ENTITY_STATS, conflictEntityStatsMap);
		
		Dashboard dashboard = new Dashboard();
		dashboard.add(KEY_SYNC_MSGS, syncMsgs);
		dashboard.add(KEY_ERRORS, errors);
		dashboard.add(KEY_CONFLICTS, conflicts);
		
		return dashboard;
	}
	
	private Integer getCount(String entityName, String modelClass, SyncOperation op) {
		return producerTemplate.requestBody("jpa:" + entityName + "?query=SELECT count(*) FROM " + entityName
		        + " WHERE modelClassName = '" + modelClass + "' AND operation = '" + op + "'",
		    null, Integer.class);
	}
	
}
