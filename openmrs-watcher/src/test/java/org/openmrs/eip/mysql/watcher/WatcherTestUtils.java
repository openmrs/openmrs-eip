package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.Constants.OPENMRS_DATASOURCE_NAME;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.AppContext;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.openmrs.eip.mysql.watcher.management.entity.SenderRetryQueueItem;

public final class WatcherTestUtils {
	
	public static boolean hasRetryItem(String tableName, String id, String dest) {
		ProducerTemplate template = AppContext.getBean(ProducerTemplate.class);
		final String type = SenderRetryQueueItem.class.getSimpleName();
		String query = "jpa:" + type + "?query=SELECT r FROM " + type + " r WHERE r.event.tableName IN ("
		        + Utils.getTablesInHierarchy(tableName) + ") AND r.event.primaryKeyId='" + id + "' AND r.route='" + dest
		        + "'";
		return template.requestBody(query, null, List.class).size() > 0;
	}
	
	public static Integer getPreviousOrderId(Integer orderId) {
		ProducerTemplate template = AppContext.getBean(ProducerTemplate.class);
		String query = "sql:SELECT previous_order_id FROM orders WHERE order_id = " + orderId + "?dataSource="
		        + OPENMRS_DATASOURCE_NAME;
		List<Map<String, Integer>> rows = template.requestBody(query, null, List.class);
		if (rows.isEmpty()) {
			return null;
		}
		
		return rows.get(0).get("previous_order_id");
	}
	
	public static SenderRetryQueueItem addRetryItem(String entityTable, String entityId, String entityUuid,
	                                                String destination) {
		SenderRetryQueueItem r = new SenderRetryQueueItem();
		r.setEvent(createEvent(entityTable, entityId, entityUuid, "c"));
		r.setRoute(destination);
		r.setExceptionType(EIPException.class.getName());
		r.setDateCreated(new Date());
		ProducerTemplate template = AppContext.getBean(ProducerTemplate.class);
		return (SenderRetryQueueItem) template.requestBody("jpa:" + SenderRetryQueueItem.class.getSimpleName(), r);
	}
	
	public static Event createEvent(String table, String pkId, String identifier, String operation) {
		Event event = new Event();
		event.setTableName(table);
		event.setPrimaryKeyId(pkId);
		event.setIdentifier(identifier);
		event.setOperation(operation);
		event.setSnapshot(false);
		return event;
	}
	
}
