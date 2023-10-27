package org.openmrs.eip.web.sender;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.utils.collections.ConcurrentHashSet;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.web.BaseDashboardGenerator;
import org.openmrs.eip.web.Dashboard;
import org.openmrs.eip.web.controller.DashboardGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(SyncProfiles.SENDER)
public class SenderDashboardGenerator extends BaseDashboardGenerator {
	
	private static final String ERROR_ENTITY_NAME = SenderRetryQueueItem.class.getSimpleName();
	
	private static final String EVENT_ENTITY_NAME = DebeziumEvent.class.getSimpleName();
	
	private static final String SYNC_ENTITY_NAME = SenderSyncMessage.class.getSimpleName();
	
	protected CamelContext camelContext;
	
	@Autowired
	public SenderDashboardGenerator(CamelContext camelContext, ProducerTemplate producerTemplate) {
		super(producerTemplate);
		this.camelContext = camelContext;
	}
	
	@Override
	public String getCategorizationProperty(String entityType) {
		if (EVENT_ENTITY_NAME.equals(entityType) || ERROR_ENTITY_NAME.equals(entityType)) {
			return "event.tableName";
		}
		
		return "tableName";
	}
	
	/**
	 * @see DashboardGenerator#generate()
	 */
	@Override
	public Dashboard generate() {
		final Map exceptionCountMap = new ConcurrentHashMap();
		final AtomicInteger totalErrorCount = new AtomicInteger();
		final AtomicInteger activeMqRelatedErrorCount = new AtomicInteger();
		final AtomicInteger mostEncounteredErrorCount = new AtomicInteger();
		final Set<Object> mostEncounteredErrors = new ConcurrentHashSet();
		final AtomicInteger totalSyncMsgCount = new AtomicInteger();
		final Map statusItemCountMap = new ConcurrentHashMap();
		
		AppUtils.getTablesToSync().parallelStream().forEach(table -> {
			Arrays.stream(SyncOperation.values()).parallel().forEach(op -> {
				//TODO Filter on route i.e. where it matches direct:out-bound-db-sync
				String tableName = table.toLowerCase();
				Integer errorCount = on(camelContext)
				        .to("jpa:" + ERROR_ENTITY_NAME + "?query=SELECT count(*) FROM " + ERROR_ENTITY_NAME
				                + " WHERE LOWER(event.tableName) = '" + tableName + "' AND event.operation = '" + op + "'")
				        .request(Integer.class);
				
				totalErrorCount.addAndGet(errorCount);
				
				Integer msgCount = on(camelContext)
				        .to("jpa:" + SYNC_ENTITY_NAME + "?query=SELECT count(*) FROM " + SYNC_ENTITY_NAME
				                + " WHERE LOWER(tableName) = '" + tableName + "' AND operation = '" + op + "'")
				        .request(Integer.class);
				
				totalSyncMsgCount.addAndGet(msgCount);
			});
		});
		
		if (totalErrorCount.get() > 0) {
			List<Object[]> items = on(camelContext).to("jpa:" + ERROR_ENTITY_NAME
			        + "?query=SELECT exceptionType, count(*) FROM " + ERROR_ENTITY_NAME + " GROUP BY exceptionType")
			        .request(List.class);
			
			items.forEach(values -> {
				final String exception = values[0].toString();
				final Integer count = Integer.valueOf(values[1].toString());
				exceptionCountMap.put(exception, count);
				
				try {
					if (ActiveMQException.class.isAssignableFrom(getClass().getClassLoader().loadClass(exception))) {
						activeMqRelatedErrorCount.addAndGet(count);
					}
				}
				catch (ClassNotFoundException e) {
					throw new EIPException("Failed to load exception class " + exception, e);
				}
				
				synchronized (this) {
					if (count >= mostEncounteredErrorCount.get()) {
						if (count > mostEncounteredErrorCount.get()) {
							mostEncounteredErrors.clear();
						}
						
						mostEncounteredErrorCount.set(count);
						mostEncounteredErrors.add(exception);
					}
				}
			});
		}
		
		if (totalSyncMsgCount.get() > 0) {
			List<Object[]> items = on(camelContext).to(
			    "jpa:" + SYNC_ENTITY_NAME + "?query=SELECT status, count(*) FROM " + SYNC_ENTITY_NAME + " GROUP BY status")
			        .request(List.class);
			
			items.forEach(values -> {
				statusItemCountMap.put(values[0], values[1]);
			});
		}
		
		Dashboard dashboard = new Dashboard();
		Map<String, Object> errors = new ConcurrentHashMap();
		errors.put("activeMqRelatedErrorCount", activeMqRelatedErrorCount);
		errors.put("mostEncounteredErrors", mostEncounteredErrors);
		errors.put("exceptionCountMap", exceptionCountMap);
		dashboard.add("errors", errors);
		
		Map<String, Object> syncMessages = new ConcurrentHashMap();
		syncMessages.put("statusItemCountMap", statusItemCountMap);
		dashboard.add("syncMessages", syncMessages);
		
		return dashboard;
	}
	
}
