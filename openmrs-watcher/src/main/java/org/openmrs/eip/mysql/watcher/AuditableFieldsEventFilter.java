package org.openmrs.eip.mysql.watcher;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.COLUMN_CHANGED_BY;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.COLUMN_DATE_CHANGED;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(WatcherConstants.EVENT_AUT_COLUMNS_FILTER_BEAN_NAME)
public class AuditableFieldsEventFilter implements EventFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(AuditableFieldsEventFilter.class);
	
	@Override
	public boolean accept(Event event, Exchange exchange) {
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
		
		Set<String> columns = new HashSet(prevState.keySet().size() + newState.keySet().size());
		if (columns.isEmpty()) {
			//Process just to be safe, not sure why column list would be empty
			return true;
		}
		
		if (!columns.contains(COLUMN_CHANGED_BY) && !columns.contains(COLUMN_DATE_CHANGED)) {
			//Table doesn't have both columns
			return true;
		}
		
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
			
			//For simplicity only detect changes for simple types
			if (prevValue != null && newValue != null && prevValue.getClass().isPrimitive()
			        && newValue.getClass().isPrimitive()) {
				
				if (prevValue.equals(newValue)) {
					modifiedColumns.remove(column);
				}
			}
		}
		
		return modifiedColumns.size() > 0;
	}
	
}
