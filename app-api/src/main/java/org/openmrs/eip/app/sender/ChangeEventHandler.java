package org.openmrs.eip.app.sender;

import java.util.Date;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.kafka.connect.data.Struct;
import org.openmrs.eip.app.CustomFileOffsetBackingStore;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.app.management.repository.DebeziumEventRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Generates a {@link DebeziumEvent} instance for the associated change event and saves it to the
 * debezium_event_queue table in the management DB.
 */
@Component("changeEventHandler")
@Profile(SyncProfiles.SENDER)
public class ChangeEventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ChangeEventHandler.class);
	
	private DebeziumEventRepository repository;
	
	@Autowired
	public ChangeEventHandler(DebeziumEventRepository repository) {
		this.repository = repository;
	}
	
	/**
	 * Processes a database change event and saved it to the database
	 * 
	 * @param tableName the affected table name
	 * @param id the affected database row id
	 * @param snapshot specifies if it is a snapshot event or not
	 * @param metadata the debezium event source metadata
	 * @param exchange the {@link Exchange} object
	 * @throws EIPException
	 */
	public void handle(String tableName, String id, boolean snapshot, Map<String, Object> metadata, Exchange exchange)
	    throws EIPException {
		
		if (CustomFileOffsetBackingStore.isDisabled()) {
			if (log.isDebugEnabled()) {
				log.debug("Deferring DB event because an error was encountered while processing a previous one");
			}
			
			return;
		}
		
		Message message = exchange.getMessage();
		String op = message.getHeader(DebeziumConstants.HEADER_OPERATION, String.class);
		
		log.info("Received DB change event: Operation=" + op + ", Metadata=" + metadata);
		
		if (!op.equals("c") && !op.equals("u") && !op.equals("d")) {
			throw new EIPException("Don't know how to handle DB event with operation: " + op);
		}
		
		boolean isSubclassTable = Utils.isSubclassTable(tableName);
		if (isSubclassTable && snapshot) {
			//We only need to process the row from the parent table during initial loading
			if (log.isTraceEnabled()) {
				log.trace("Skipping " + tableName + " snapshot event");
			}
			
			return;
		}
		
		Event event = new Event();
		event.setTableName(tableName);
		event.setPrimaryKeyId(id);
		event.setOperation(op);
		event.setSnapshot(snapshot);
		if (!isSubclassTable) {
			String uuid;
			if (op.equals("d")) {
				uuid = message.getHeader(DebeziumConstants.HEADER_BEFORE, Struct.class).getString("uuid");
			} else {
				uuid = message.getBody(Struct.class).getString("uuid");
			}
			
			event.setIdentifier(uuid);
		}
		
		DebeziumEvent debeziumEvent = new DebeziumEvent();
		debeziumEvent.setEvent(event);
		debeziumEvent.setDateCreated(new Date());
		
		if (log.isDebugEnabled()) {
			log.debug("Saving debezium event to event queue: " + debeziumEvent);
		}
		
		repository.save(debeziumEvent);
		
		log.info("Debezium event saved to event queue");
	}
	
}
