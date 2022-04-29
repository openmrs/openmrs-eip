package org.openmrs.eip.mysql.watcher.route;

import static org.apache.camel.LoggingLevel.DEBUG;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DBZM_MSG_PROCESSOR;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.DEBEZIUM_ROUTE_ID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.EX_PROP_SKIP;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.ID_SETTING_PROCESSOR;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.openmrs.eip.mysql.watcher.CustomFileOffsetBackingStore;
import org.openmrs.eip.mysql.watcher.DebeziumMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configures, invokes the {@link org.apache.camel.component.debezium.DebeziumMySqlComponent} and
 * registers a listener to be notified whenever a DB event is received from the debezium engine. It
 * also registers a {@link DebeziumMessageProcessor} instance to pre-process every event before
 * notifying the main db event listener.
 */
public class DebeziumRoute extends RouteBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(DebeziumRoute.class);
	
	protected static final String ROUTE_ID_EVENT_LISTENER = "debezium-event-listener";
	
	protected static final String URI_EVENT_LISTENER = "direct:" + ROUTE_ID_EVENT_LISTENER;
	
	private String fromUri;
	
	private String errorHandlerRef;
	
	public DebeziumRoute(String fromUri, String errorHandlerRef) {
		this.fromUri = fromUri;
		this.errorHandlerRef = errorHandlerRef;
	}
	
	@Override
	public void configure() {
		logger.info("Starting debezium...");
		
		RouteDefinition routeDef = from(fromUri).routeId(DEBEZIUM_ROUTE_ID);
		
		logger.info("Setting debezium route handler to: " + errorHandlerRef);
		
		routeDef.setErrorHandlerRef(errorHandlerRef);
		
		routeDef.choice()//Start outer choice
		        
		        .when(exchange -> !CustomFileOffsetBackingStore.isDisabled())
		        
		        .choice()//Start inner choice
		        
		        .when(exchange -> !exchange.getProperty(EX_PROP_SKIP, false, Boolean.class))
		        
		        .process(DBZM_MSG_PROCESSOR)
		        
		        .process(ID_SETTING_PROCESSOR)
		        
		        .to(URI_EVENT_LISTENER)
		        
		        .endChoice()//End inner choice
		        
		        .otherwise()
		        
		        .log(DEBUG, "Deferring DB event because an error was encountered while processing a previous one")
		        
		        .end();//End outer choice
	}
	
}
