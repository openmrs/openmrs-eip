package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.common.CommonConstants;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This Processor is called to attempt to resolve and set the entity identifier on the event object.
 * In case of a subclass table, this processor retrieves the uuid from it's parent table e.g
 * person.uuid value for patient table
 */
@Component("id-setting-event-processor")
public class IdentifierSettingProcessor implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(IdentifierSettingProcessor.class);
	
	private static final List<String> SUBCLASS_TABLES = Arrays.asList("test_order", "drug_order", "patient");
	
	@Override
	public void process(Exchange exchange) {
		Event event = exchange.getProperty(PROP_EVENT, Event.class);
		if (StringUtils.isNotBlank(event.getIdentifier())) {
			return;
		}
		
		Object uuid;
		if ("d".equals(event.getOperation())) {
			uuid = event.getPreviousState().get(WatcherConstants.FIELD_UUID);
		} else {
			uuid = event.getCurrentState().get(WatcherConstants.FIELD_UUID);
		}
		
		if (uuid != null) {
			event.setIdentifier(uuid.toString());
		} else if (isSubclassTable(event.getTableName())) {
			//For a subclass table, fetch the identifier(uuid) from the parent table e.g person.uuid value for patient
			//table joining on the FK
			//TODO Allow external code to register custom subclass tables along with the referenced tables and columns
			String refTable;
			String refColumn;
			if ("patient".equalsIgnoreCase(event.getTableName())) {
				refTable = "person";
				refColumn = "person_id";
			} else {
				refTable = "orders";
				refColumn = "order_id";
			}
			
			logger.debug("Looking up uuid for " + event.getTableName() + " from " + refTable + " table");
			
			String query = "SELECT uuid FROM " + refTable + " WHERE " + refColumn + "=" + event.getPrimaryKeyId()
			        + "?dataSource=" + CommonConstants.OPENMRS_DATASOURCE_NAME;
			List<Map> rows = DefaultFluentProducerTemplate.on(exchange.getContext()).to("sql:" + query).request(List.class);
			event.setIdentifier(rows.get(0).get(WatcherConstants.FIELD_UUID).toString());
		}
		
		//TODO Should we run a select to fetch the uuid from the DB as the final option?
		
		if (StringUtils.isBlank(event.getIdentifier())) {
			throw new EIPException("Failed to determine identifier for the entity associated with the event: " + event);
		}
	}
	
	private boolean isSubclassTable(String tableName) {
		return SUBCLASS_TABLES.contains(tableName.toLowerCase());
	}
	
}
