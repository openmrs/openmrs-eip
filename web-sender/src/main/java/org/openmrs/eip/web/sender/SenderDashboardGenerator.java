package org.openmrs.eip.web.sender;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.activemq.artemis.utils.collections.ConcurrentHashSet;
import org.apache.camel.CamelContext;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SenderRetryQueueItem;
import org.openmrs.eip.component.DatabaseOperation;
import org.openmrs.eip.web.Dashboard;
import org.openmrs.eip.web.contoller.DashboardGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SenderDashboardGenerator implements DashboardGenerator {
	
	private static final String ENTITY_NAME = SenderRetryQueueItem.class.getSimpleName();
	
	private static final Set<String> CONN_EX_CLASSES;
	
	static {
		CONN_EX_CLASSES = new HashSet();
		//CONN_EX_CLASSES.add()
	}
	
	protected CamelContext camelContext;
	
	@Autowired
	public SenderDashboardGenerator(CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	/**
	 * @see DashboardGenerator#generate()
	 */
	@Override
	public Dashboard generate() {
		final Map<String, Map> tableStatsMap = new ConcurrentHashMap();
		final Map exceptionCountMap = new ConcurrentHashMap();
		final AtomicInteger totalErrorCount = new AtomicInteger();
		final AtomicInteger connectionErrorCount = new AtomicInteger();
		final AtomicInteger mostEncounteredErrorCount = new AtomicInteger();
		final Set<Object> mostEncounteredErrors = new ConcurrentHashSet();
		
		AppUtils.getTablesToSync().parallelStream().forEach(table -> {
			Arrays.stream(DatabaseOperation.values()).parallel().forEach(op -> {
				//TODO Filter on route i.e. where it matches direct:out-bound-db-sync
				String tableName = table.toLowerCase();
				Integer count = on(camelContext)
				        .to("jpa:" + ENTITY_NAME + "?query=SELECT count(*) FROM " + ENTITY_NAME
				                + " WHERE LOWER(event.tableName) = '" + tableName + "' AND event.operation = '" + op + "'")
				        .request(Integer.class);
				
				totalErrorCount.addAndGet(count);
				
				if (count > 0) {
					synchronized (this) {
						if (tableStatsMap.get(tableName) == null) {
							tableStatsMap.put(tableName, new ConcurrentHashMap());
						}
					}
					
					tableStatsMap.get(tableName).put(op, count);
				}
			});
		});
		
		if (!tableStatsMap.isEmpty()) {
			List<Object[]> items = on(camelContext).to("jpa:" + ENTITY_NAME + "?query=SELECT exceptionType, count(*) FROM "
			        + ENTITY_NAME + " GROUP BY exceptionType").request(List.class);
			
			items.parallelStream().forEach(values -> {
				final Object exception = values[0];
				final Integer count = Integer.valueOf(values[1].toString());
				exceptionCountMap.put(exception, count);
				if (CONN_EX_CLASSES.contains(exception)) {
					connectionErrorCount.addAndGet(count);
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
		
		Dashboard dashboard = new Dashboard();
		Map<String, Object> errors = new ConcurrentHashMap(2);
		errors.put("totalCount", totalErrorCount);
		errors.put("connectionErrorCount", connectionErrorCount);
		errors.put("mostEncounteredErrors", mostEncounteredErrors);
		errors.put("tableStatsMap", tableStatsMap);
		errors.put("exceptionCountMap", exceptionCountMap);
		dashboard.add("errors", errors);
		dashboard.add("pending", Collections.emptyList());
		
		return dashboard;
	}
	
}
