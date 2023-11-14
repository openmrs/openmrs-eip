package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.Constants.OPENMRS_DATASOURCE_NAME;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema.Type;
import org.apache.kafka.connect.data.Struct;
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
	
	/**
	 * Gets the previous order id for the given order id
	 *
	 * @param orderId the order id
	 * @return the previous order id
	 */
	public static Integer getPreviousOrderId(Integer orderId) {
		ProducerTemplate template = AppContext.getBean(ProducerTemplate.class);
		String query = "sql:SELECT previous_order_id FROM orders WHERE order_id = " + orderId + "?dataSource=#"
		        + OPENMRS_DATASOURCE_NAME;
		List<Map<String, Integer>> rows = template.requestBody(query, null, List.class);
		if (rows.isEmpty()) {
			return null;
		}
		
		return rows.get(0).get("previous_order_id");
	}
	
	public static SenderRetryQueueItem addRetryItem(String entityTable, String entityId, String entityUuid,
	        String destination) {
		SenderRetryQueueItem retryQueueItem = new SenderRetryQueueItem();
		retryQueueItem.setEvent(createEvent(entityTable, entityId, entityUuid, "c"));
		retryQueueItem.setRoute(destination);
		retryQueueItem.setExceptionType(EIPException.class.getName());
		retryQueueItem.setDateCreated(new Date());
		ProducerTemplate template = AppContext.getBean(ProducerTemplate.class);
		return (SenderRetryQueueItem) template.requestBody("jpa:" + SenderRetryQueueItem.class.getSimpleName(),
		    retryQueueItem);
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
	
	public static Exchange createExchange(String table, Integer id, Snapshot snapshot) {
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		final String idColumn = table + "_id";
		Field idField = createField(idColumn, 0, Type.INT32);
		Struct idStruct = createStruct(Collections.singletonList(idField));
		idStruct.put(idColumn, id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, table);
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, snapshot.getRawValue());
		
		message.setHeader(DebeziumConstants.HEADER_KEY, idStruct);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		
		return exchange;
	}
	
	public static Field createField(String name, int index, Type type) {
		return new Field(name, index, new ConnectSchema(type));
	}
	
	public static ConnectSchema createSchema(List<Field> fields) {
		return new ConnectSchema(Type.STRUCT, false, null, null, null, null, null, fields, null, null);
	}
	
	public static Struct createStruct(List<Field> fields) {
		return new Struct(createSchema(fields));
	}
	
}
