package org.openmrs.eip.mysql.watcher;

import org.apache.camel.Exchange;

/**
 * An implementation of this interface provides a filter to use to decide if a database change event
 * should be processed i.e. when the predicate passes, the event is processed otherwise it is
 * skipped. Filters are registered as spring beans and are auto discovered by the framework,
 * multiple implementations can be provided. In case of multiple filters, if any filter returns
 * false, the event is skipped.
 * 
 * @since 3.1.0
 */
public interface EventFilter {
	
	/**
	 * Evaluates if the specified event should be processed or not
	 * 
	 * @param event the event to evaluate
	 * @param exchange the {@link Exchange} object associated to the event
	 * @return true if the the event should be processed otherwise false
	 */
	boolean accept(Event event, Exchange exchange);
	
}
