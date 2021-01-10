package org.openmrs.eip.app;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.commons.collections.map.HashedMap;
import org.apache.kafka.connect.data.Struct;
import org.openmrs.eip.component.entity.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("publisher-processor")
public class PublisherProcessor implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(PublisherProcessor.class);

    @Override
    public void process(Exchange exchange) {
        Message message = exchange.getMessage();
        Event event = new Event();
        Struct primaryKeyStruct = message.getHeader(DebeziumConstants.HEADER_KEY, Struct.class);
        //TODO Take care of situation where a table has a composite FK because fields length will be > 1
        event.setPrimaryKeyId(primaryKeyStruct.get(primaryKeyStruct.schema().fields().get(0)).toString());
        Map<String, Object> sourceMetadata = message.getHeader(DebeziumConstants.HEADER_SOURCE_METADATA, Map.class);
        event.setTableName(sourceMetadata.get(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE).toString());
        event.setOperation(message.getHeader(DebeziumConstants.HEADER_OPERATION, String.class));

        Object snapshot = sourceMetadata.getOrDefault(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "");
        if (!"false".equalsIgnoreCase(snapshot.toString())) {
            event.setSnapshot(true);
        }

        logger.info("Received debezium event: " + event);

        if (message.getBody() != null) {
            Map<String, Object> currentState = new HashedMap();
            Struct bodyStruct = message.getBody(Struct.class);
            bodyStruct.schema().fields().forEach(field -> currentState.put(field.name(), bodyStruct.get(field)));
            event.setCurrentState(currentState);
        }

        Struct beforeStruct = message.getHeader(DebeziumConstants.HEADER_BEFORE, Struct.class);
        if (beforeStruct != null) {
            Map<String, Object> beforeState = new HashedMap();
            beforeStruct.schema().fields().forEach(field -> beforeState.put(field.name(), beforeStruct.get(field)));
            event.setPreviousState(beforeState);
        }

        message.setBody(event);
    }

}
