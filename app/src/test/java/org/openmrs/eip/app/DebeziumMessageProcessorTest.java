package org.openmrs.eip.app;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema.Type;
import org.apache.kafka.connect.data.Struct;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.component.entity.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.connect.data.Schema.Type.STRUCT;
import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.OpenmrsEipConstants.HEADER_EVENT;

public class DebeziumMessageProcessorTest {

    private Processor processor = new DebeziumMessageProcessor();

    @Test
    public void process_shouldCreateAnEventAndAddItAsAHeaderForAnUpdate() throws Exception {
        final Integer id = 2;
        final String op = "u";
        final String table = "visit";
        final Integer visitTypeId = 3;
        final String prevVoidReason = "Testing old";
        final String newVoidReason = "Testing new";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
        Field visitTypeIdField = new Field("visit_type_id", 0, new ConnectSchema(Type.INT32));
        Field voidReasonField = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
        List<Field> beforeFields = new ArrayList();
        beforeFields.add(visitTypeIdField);
        beforeFields.add(voidReasonField);
        Struct beforeState = new Struct(new ConnectSchema(STRUCT, false, null, "before", null, null, null, beforeFields, null, null));
        beforeState.put(visitTypeIdField, visitTypeId);
        beforeState.put(voidReasonField, prevVoidReason);

        Field voidReasonCurrent = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
        List<Field> bodyFields = new ArrayList();
        bodyFields.add(visitTypeIdField);
        bodyFields.add(voidReasonCurrent);
        Struct currentState = new Struct(new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
        currentState.put(visitTypeIdField, visitTypeId);
        currentState.put(voidReasonField, newVoidReason);

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
        message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
        message.setHeader(DebeziumConstants.HEADER_BEFORE, beforeState);
        message.setBody(currentState);

        processor.process(exchange);

        Event event = exchange.getMessage().getHeader(HEADER_EVENT, Event.class);
        assertEquals(table, event.getTableName());
        assertEquals(id.toString(), event.getPrimaryKeyId());
        assertEquals(op, event.getOperation());
        Assert.assertFalse(event.getSnapshot());
        Assert.assertEquals(2, event.getPreviousState().size());
        Assert.assertEquals(visitTypeId, event.getPreviousState().get("visit_type_id"));
        Assert.assertEquals(prevVoidReason, event.getPreviousState().get("void_reason"));
        Assert.assertEquals(2, event.getCurrentState().size());
        Assert.assertEquals(visitTypeId, event.getCurrentState().get("visit_type_id"));
        Assert.assertEquals(newVoidReason, event.getCurrentState().get("void_reason"));
    }

    @Test
    public void process_shouldCreateAnEventAndAddItAsAHeaderForAnInsert() throws Exception {
        final Integer id = 2;
        final String op = "i";
        final String table = "visit";
        final Integer visitTypeId = 3;
        final String voidReason = "Testing";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
        Field visitTypeIdField = new Field("visit_type_id", 0, new ConnectSchema(Type.INT32));
        Field voidReasonField = new Field("void_reason", 1, new ConnectSchema(Type.STRING));

        Field currentVoidReason = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
        List<Field> bodyFields = new ArrayList();
        bodyFields.add(visitTypeIdField);
        bodyFields.add(currentVoidReason);
        Struct currentState = new Struct(new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
        currentState.put(visitTypeIdField, visitTypeId);
        currentState.put(voidReasonField, voidReason);

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
        message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
        message.setBody(currentState);

        processor.process(exchange);

        Event event = exchange.getMessage().getHeader(HEADER_EVENT, Event.class);
        assertEquals(table, event.getTableName());
        assertEquals(id.toString(), event.getPrimaryKeyId());
        assertEquals(op, event.getOperation());
        Assert.assertFalse(event.getSnapshot());
        Assert.assertNull(event.getPreviousState());
        Assert.assertEquals(2, event.getCurrentState().size());
        Assert.assertEquals(visitTypeId, event.getCurrentState().get("visit_type_id"));
        Assert.assertEquals(voidReason, event.getCurrentState().get("void_reason"));
    }

    @Test
    public void process_shouldCreateAnEventAndAddItAsAHeaderForADelete() throws Exception {
        final Integer id = 2;
        final String op = "d";
        final String table = "visit";
        final Integer visitTypeId = 3;
        final String voidReason = "Testing";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
        Field visitTypeIdField = new Field("visit_type_id", 0, new ConnectSchema(Type.INT32));
        Field voidReasonField = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
        List<Field> beforeFields = new ArrayList();
        beforeFields.add(visitTypeIdField);
        beforeFields.add(voidReasonField);
        Struct beforeState = new Struct(new ConnectSchema(STRUCT, false, null, "before", null, null, null, beforeFields, null, null));
        beforeState.put(visitTypeIdField, visitTypeId);
        beforeState.put(voidReasonField, voidReason);

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
        message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
        message.setHeader(DebeziumConstants.HEADER_BEFORE, beforeState);

        processor.process(exchange);

        Event event = exchange.getMessage().getHeader(HEADER_EVENT, Event.class);
        assertEquals(table, event.getTableName());
        assertEquals(id.toString(), event.getPrimaryKeyId());
        assertEquals(op, event.getOperation());
        Assert.assertFalse(event.getSnapshot());
        Assert.assertEquals(2, event.getPreviousState().size());
        Assert.assertEquals(visitTypeId, event.getPreviousState().get("visit_type_id"));
        Assert.assertEquals(voidReason, event.getPreviousState().get("void_reason"));
        Assert.assertNull(event.getCurrentState());
    }

    @Test
    public void process_shouldSetSnapshotToFalseForTheEvent() throws Exception {
        final Integer id = 2;
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);

        processor.process(exchange);

        Assert.assertFalse(exchange.getMessage().getHeader(HEADER_EVENT, Event.class).getSnapshot());
    }

    @Test
    public void process_shouldSetSnapshotToTrueForTheEventIfNotSpecified() throws Exception {
        final Integer id = 2;
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);

        processor.process(exchange);

        Assert.assertTrue(exchange.getMessage().getHeader(HEADER_EVENT, Event.class).getSnapshot());
    }

    @Test
    public void process_shouldSetSnapshotToTrueForTheEventIfNotSpecifiedAsFalse() throws Exception {
        final Integer id = 2;
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "");

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);

        processor.process(exchange);

        Assert.assertTrue(exchange.getMessage().getHeader(HEADER_EVENT, Event.class).getSnapshot());
    }

    @Test
    public void process_shouldSetSnapshotToTrueForTheEventIfItIsSetToTrueOnTheMessage() throws Exception {
        final Integer id = 2;
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "true");

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);

        processor.process(exchange);

        Assert.assertTrue(exchange.getMessage().getHeader(HEADER_EVENT, Event.class).getSnapshot());
    }

    @Test
    public void process_shouldSetSnapshotToFalseForTheEventIfItIsSetToFalseOnTheMessage() throws Exception {
        final Integer id = 2;
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message message = new DefaultMessage(exchange);
        exchange.setMessage(message);
        Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
        List<Field> fields = new ArrayList();
        fields.add(visitId);
        Struct primaryKey = new Struct(new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
        primaryKey.put("visit_id", id);
        Map<String, Object> sourceMetadata = new HashMap();
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_TABLE, "visit");
        sourceMetadata.put(OpenmrsEipConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");

        message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
        message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);

        processor.process(exchange);

        Assert.assertFalse(exchange.getMessage().getHeader(HEADER_EVENT, Event.class).getSnapshot());
    }

}
