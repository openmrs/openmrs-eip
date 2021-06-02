package org.openmrs.eip.web.sender;

import static org.apache.camel.impl.engine.DefaultFluentProducerTemplate.on;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
		
		AppUtils.getTablesToSync().parallelStream().forEach(table -> {
			Arrays.stream(DatabaseOperation.values()).parallel().forEach(op -> {
				//TODO Filter on route i.e. where it matches direct:out-bound-db-sync
				String tableName = table.toLowerCase();
				Integer count = on(camelContext)
				        .to("jpa:" + ENTITY_NAME + "?query=SELECT count(*) FROM " + ENTITY_NAME
				                + " WHERE LOWER(event.tableName) = '" + tableName + "' AND event.operation = '" + op + "'")
				        .request(Integer.class);
				
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
				exceptionCountMap.put(values[0] != null ? values[0] : "uncategorized", values[1]);
			});
		}
		
		Dashboard dashboard = new Dashboard();
		Map<String, Map> errors = new ConcurrentHashMap(2);
		errors.put("tableStatsMap", tableStatsMap);
		errors.put("exceptionCountMap", exceptionCountMap);
		dashboard.add("errors", errors);
		dashboard.add("pending", Collections.emptyList());
		
		return dashboard;
	}
	
}
