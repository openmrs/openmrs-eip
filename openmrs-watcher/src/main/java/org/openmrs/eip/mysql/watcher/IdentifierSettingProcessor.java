package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT;

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.Constants;
import org.openmrs.eip.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This Processor is called to attempt to resolve and set the entity identifier on the event object.
 * In case of a subclass table, this processor retrieves the uuid from it's parent table e.g
 * person.uuid value for patient table
 */
@Component(WatcherConstants.ID_SETTING_PROCESSOR)
public class IdentifierSettingProcessor implements Processor {
	
	private static final Logger logger = LoggerFactory.getLogger(IdentifierSettingProcessor.class);
	
	@Override
	public void process(Exchange exchange) {
		Event event = exchange.getProperty(PROP_EVENT, Event.class);
		if (logger.isDebugEnabled()) {
			logger.debug("Received: " + event);
		}
		
		if (StringUtils.isNotBlank(event.getIdentifier())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Event object already has the identifier set");
			}
			
			return;
		}
		
		//TODO For subclass events and retry items previous and current state are null
		Object uuid = null;
		if ("d".equals(event.getOperation()) && event.getPreviousState() != null) {
			uuid = event.getPreviousState().get(WatcherConstants.FIELD_UUID);
		} else if (event.getCurrentState() != null) {
			uuid = event.getCurrentState().get(WatcherConstants.FIELD_UUID);
		}
		
		if (uuid != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Setting the identifier to the uuid read from the debezium payload");
			}
			
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
			        + "?dataSource=" + Constants.OPENMRS_DATASOURCE_NAME;
			List<Map> rows = DefaultFluentProducerTemplate.on(exchange.getContext()).to("sql:" + query).request(List.class);
			if (!rows.isEmpty()) {
				event.setIdentifier(rows.get(0).get(WatcherConstants.FIELD_UUID).toString());
			} else {
				logger.info("Failed to find row in parent table: " + refTable);
			}
		}
		
		//TODO Should we run a select to fetch the uuid from the DB as the final option?
		
		if (StringUtils.isBlank(event.getIdentifier())) {
			if (isSubclassTable(event.getTableName()) && event.getOperation().equals("d")) {
				logger.info("Can't determine the identifier for a deleted subclass row with no parent row");
			} else {
				throw new EIPException("Failed to determine identifier for the entity associated with the event: " + event);
			}
		}
	}
	
	private boolean isSubclassTable(String tableName) {
		return Constants.SUBCLASS_TABLES.contains(tableName.toLowerCase());
	}
	
}
