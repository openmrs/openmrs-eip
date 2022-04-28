package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.COLUMN_CHANGED_BY;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.COLUMN_DATE_CHANGED;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_AUDIT_FILTER_TABLES;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Auditable entities in OpenMRS have changed_by and date_changed columns which are updated whenever
 * an event is updated but sometimes the logic in the OpenMRS API that auto updates these column
 * values even when the row has no modifications, an instance of this class filters them out.
 */
@Component(WatcherConstants.EVENT_AUT_COLUMNS_FILTER_BEAN_NAME)
public class AuditableEventFilter implements EventFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditableEventFilter.class);
	
	private Set<String> tables;
	
	public AuditableEventFilter(@Value("${" + PROP_AUDIT_FILTER_TABLES + ":}") List<String> tables) {
		this.tables = tables.stream().filter(t -> StringUtils.isNotBlank(t)).map(t -> t.trim().toLowerCase())
		        .collect(Collectors.toSet());
	}
	
	@Override
	public boolean accept(Event event, Exchange exchange) {
		if (tables.isEmpty() || !tables.contains(event.getTableName().toLowerCase())) {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping event for a non filtered table");
			}
			
			return true;
		}
		
		if (!"u".equals(event.getOperation())) {
			if (logger.isTraceEnabled()) {
				logger.trace("Skipping non update event");
			}
			
			return true;
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("Checking if only changed_by and date_changed column values changed");
		}
		
		Map<String, Object> prevState = event.getPreviousState();
		Map<String, Object> newState = event.getCurrentState();
		
		Set<String> columns = new HashSet(prevState.keySet());
		columns.addAll(newState.keySet());
		
		if (!columns.contains(COLUMN_CHANGED_BY) || !columns.contains(COLUMN_DATE_CHANGED)) {
			//Table doesn't have one of the columns (not an auditable entity), process by default.
			//Should also take care of a table with no columns except the PK column, it is hypothetical but process 
			//just to be safe, not sure why column list would be empty though
			return true;
		}
		
		//Safe to assume all columns values changed
		Set<String> modifiedColumns = new HashSet(prevState.keySet());
		modifiedColumns.addAll(newState.keySet());
		modifiedColumns.remove(COLUMN_CHANGED_BY);
		modifiedColumns.remove(COLUMN_DATE_CHANGED);
		
		for (String column : columns) {
			Object prevValue = prevState.get(column);
			Object newValue = newState.get(column);
			if (prevValue == null && newValue == null) {
				modifiedColumns.remove(column);
				continue;
			}
			
			if (prevValue != null && newValue != null) {
				//For simplicity only detect changes for columns of simple types
				boolean isPrevValidValidType = prevValue.getClass().isPrimitive() || prevValue instanceof String;
				boolean isNewValidValidType = newValue.getClass().isPrimitive() || prevValue instanceof String;
				
				if (prevValue.equals(newValue) && isPrevValidValidType && isNewValidValidType) {
					modifiedColumns.remove(column);
				}
			}
		}
		
		return modifiedColumns.size() > 0;
	}
	
}
