package org.openmrs.eip.mysql.watcher;

import static org.apache.kafka.connect.data.Schema.Type.STRUCT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.EX_PROP_SKIP;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.FIELD_UUID;
import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_EVENT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.DefaultFluentProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema.Type;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openmrs.eip.EIPException;

public class DebeziumMessageProcessorTest {
	
	private Processor processor = new DebeziumMessageProcessor(Collections.emptyList());
	
	@Test
	public void process_shouldCreateAnEventAndAddItAsAHeaderForAnUpdate() throws Exception {
		final Integer id = 2;
		final String uuid = "1296b0dc-440a-11e6-a65c-00e04c680037";
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
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
		Field visitTypeIdField = new Field("visit_type_id", 0, new ConnectSchema(Type.INT32));
		Field voidReasonField = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
		Field uuidField = new Field(FIELD_UUID, 2, new ConnectSchema(Type.STRING));
		List<Field> beforeFields = new ArrayList();
		beforeFields.add(visitTypeIdField);
		beforeFields.add(voidReasonField);
		beforeFields.add(uuidField);
		Struct beforeState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "before", null, null, null, beforeFields, null, null));
		beforeState.put(visitTypeIdField, visitTypeId);
		beforeState.put(voidReasonField, prevVoidReason);
		beforeState.put(uuidField, uuid);
		
		Field voidReasonCurrent = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = new ArrayList();
		bodyFields.add(visitTypeIdField);
		bodyFields.add(voidReasonCurrent);
		bodyFields.add(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(visitTypeIdField, visitTypeId);
		currentState.put(voidReasonField, newVoidReason);
		currentState.put(uuidField, uuid);
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		message.setHeader(DebeziumConstants.HEADER_BEFORE, beforeState);
		message.setBody(currentState);
		
		processor.process(exchange);
		
		Event event = exchange.getProperty(PROP_EVENT, Event.class);
		assertEquals(table, event.getTableName());
		assertEquals(id.toString(), event.getPrimaryKeyId());
		assertEquals(op, event.getOperation());
		assertFalse(event.getSnapshot());
		assertEquals(3, event.getPreviousState().size());
		assertEquals(visitTypeId, event.getPreviousState().get("visit_type_id"));
		assertEquals(prevVoidReason, event.getPreviousState().get("void_reason"));
		assertEquals(uuid, event.getPreviousState().get(FIELD_UUID));
		assertEquals(3, event.getCurrentState().size());
		assertEquals(visitTypeId, event.getCurrentState().get("visit_type_id"));
		assertEquals(newVoidReason, event.getCurrentState().get("void_reason"));
		assertEquals(uuid, event.getCurrentState().get(FIELD_UUID));
	}
	
	@Test
	public void process_shouldCreateAnEventAndAddItAsAHeaderForAnInsert() throws Exception {
		final Integer id = 2;
		final String uuid = "1296b0dc-440a-11e6-a65c-00e04c680037";
		final String op = "c";
		final String table = "visit";
		final Integer visitTypeId = 3;
		final String voidReason = "Testing";
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
		List<Field> fields = new ArrayList();
		fields.add(visitId);
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
		Field visitTypeIdField = new Field("visit_type_id", 0, new ConnectSchema(Type.INT32));
		Field voidReasonField = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
		
		Field currentVoidReason = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = new ArrayList();
		bodyFields.add(visitTypeIdField);
		bodyFields.add(currentVoidReason);
		Field uuidField = new Field(FIELD_UUID, 2, new ConnectSchema(Type.STRING));
		bodyFields.add(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(visitTypeIdField, visitTypeId);
		currentState.put(voidReasonField, voidReason);
		currentState.put(uuidField, uuid);
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		message.setBody(currentState);
		
		processor.process(exchange);
		
		Event event = exchange.getProperty(PROP_EVENT, Event.class);
		assertEquals(table, event.getTableName());
		assertEquals(id.toString(), event.getPrimaryKeyId());
		assertNull(event.getIdentifier());
		assertEquals(op, event.getOperation());
		assertFalse(event.getSnapshot());
		assertNull(event.getPreviousState());
		assertEquals(3, event.getCurrentState().size());
		assertEquals(visitTypeId, event.getCurrentState().get("visit_type_id"));
		assertEquals(voidReason, event.getCurrentState().get("void_reason"));
		assertEquals(uuid, event.getCurrentState().get(FIELD_UUID));
	}
	
	@Test
	public void process_shouldCreateAnEventAndAddItAsAHeaderForADelete() throws Exception {
		final Integer id = 2;
		final String uuid = "1296b0dc-440a-11e6-a65c-00e04c680037";
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
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
		Field visitTypeIdField = new Field("visit_type_id", 0, new ConnectSchema(Type.INT32));
		Field voidReasonField = new Field("void_reason", 1, new ConnectSchema(Type.STRING));
		Field uuidField = new Field(FIELD_UUID, 2, new ConnectSchema(Type.STRING));
		List<Field> beforeFields = new ArrayList();
		beforeFields.add(visitTypeIdField);
		beforeFields.add(voidReasonField);
		beforeFields.add(uuidField);
		Struct beforeState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "before", null, null, null, beforeFields, null, null));
		beforeState.put(visitTypeIdField, visitTypeId);
		beforeState.put(voidReasonField, voidReason);
		beforeState.put(uuidField, uuid);
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		message.setHeader(DebeziumConstants.HEADER_BEFORE, beforeState);
		
		processor.process(exchange);
		
		Event event = exchange.getProperty(PROP_EVENT, Event.class);
		assertEquals(table, event.getTableName());
		assertEquals(id.toString(), event.getPrimaryKeyId());
		assertNull(event.getIdentifier());
		assertEquals(op, event.getOperation());
		assertFalse(event.getSnapshot());
		assertEquals(3, event.getPreviousState().size());
		assertEquals(visitTypeId, event.getPreviousState().get("visit_type_id"));
		assertEquals(voidReason, event.getPreviousState().get("void_reason"));
		assertEquals(uuid, event.getPreviousState().get(FIELD_UUID));
		assertNull(event.getCurrentState());
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
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		Field uuidField = new Field(FIELD_UUID, 0, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = Collections.singletonList(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(uuidField, "some-uuid");
		message.setBody(currentState);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		
		processor.process(exchange);
		
		assertFalse(exchange.getProperty(PROP_EVENT, Event.class).getSnapshot());
	}
	
	@Test
	public void process_shouldSetSnapshotToTrueForTheEventIfNotSpecified() throws Exception {
		final Integer id = 2;
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
		List<Field> fields = new ArrayList<>();
		fields.add(visitId);
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		Field uuidField = new Field(FIELD_UUID, 0, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = Collections.singletonList(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(uuidField, "some-uuid");
		message.setBody(currentState);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		
		processor.process(exchange);
		
		assertTrue(exchange.getProperty(PROP_EVENT, Event.class).getSnapshot());
	}
	
	@Test
	public void process_shouldSetSnapshotToTrueForTheEventIfNotSpecifiedAsFalse() throws Exception {
		final Integer id = 2;
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
		List<Field> fields = new ArrayList<>();
		fields.add(visitId);
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, "");
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		Field uuidField = new Field(FIELD_UUID, 0, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = Collections.singletonList(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(uuidField, "some-uuid");
		message.setBody(currentState);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		
		processor.process(exchange);
		
		assertTrue(exchange.getProperty(PROP_EVENT, Event.class).getSnapshot());
	}
	
	@Test
	public void process_shouldSetSnapshotToTrueForTheEventIfItIsSetToTrueOnTheMessage() throws Exception {
		final Integer id = 2;
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
		List<Field> fields = new ArrayList<>();
		fields.add(visitId);
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, "true");
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		Field uuidField = new Field(FIELD_UUID, 0, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = Collections.singletonList(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(uuidField, "some-uuid");
		message.setBody(currentState);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		
		processor.process(exchange);
		
		assertTrue(exchange.getProperty(PROP_EVENT, Event.class).getSnapshot());
	}
	
	@Test
	public void process_shouldSetSnapshotToFalseForTheEventIfItIsSetToFalseOnTheMessage() throws Exception {
		final Integer id = 2;
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		Field visitId = new Field("visit_id", 0, new ConnectSchema(Type.INT32));
		List<Field> fields = new ArrayList<>();
		fields.add(visitId);
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("visit_id", id);
		Map<String, Object> sourceMetadata = new HashMap<>();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "visit");
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_SNAPSHOT, "false");
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		Field uuidField = new Field(FIELD_UUID, 0, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = Collections.singletonList(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(uuidField, "some-uuid");
		message.setBody(currentState);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		
		processor.process(exchange);
		
		assertFalse(exchange.getProperty(PROP_EVENT, Event.class).getSnapshot());
	}
	
	@Test
	public void process_shouldFailForAnUnSupportedDbOperation() throws Exception {
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "r");
		
		assertThrows(EIPException.class, () -> processor.process(exchange));
	}
	
	@Test
	@Disabled
	public void process_shouldSetIdentifierForASubclassTable() throws Exception {
		final Integer id = 2;
		CamelContext mockCamelContext = Mockito.mock(CamelContext.class);
		FluentProducerTemplate mockTemplate = Mockito.mock(FluentProducerTemplate.class);
		mockStatic(DefaultFluentProducerTemplate.class);
		when(DefaultFluentProducerTemplate.on(mockCamelContext)).thenReturn(mockTemplate);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		Field visitId = new Field("patient_id", 0, new ConnectSchema(Type.INT32));
		List<Field> fields = new ArrayList();
		fields.add(visitId);
		Struct primaryKey = new Struct(
		        new ConnectSchema(Type.STRUCT, false, null, "key", null, null, null, fields, null, null));
		primaryKey.put("patient_id", id);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put(WatcherConstants.DEBEZIUM_FIELD_TABLE, "patient");
		
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		Field uuidField = new Field(FIELD_UUID, 0, new ConnectSchema(Type.STRING));
		List<Field> bodyFields = Collections.singletonList(uuidField);
		Struct currentState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "body", null, null, null, bodyFields, null, null));
		currentState.put(uuidField, "some-uuid");
		message.setBody(currentState);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		
		processor.process(exchange);
	}
	
	@Test
	public void process_shouldNotSetSkipPropertyIfAllFiltersReturnTrue() throws Exception {
		Exchange exchange = WatcherTestUtils.createExchange("visit", 1, Snapshot.FALSE);
		EventFilter filter1 = Mockito.mock(EventFilter.class);
		EventFilter filter2 = Mockito.mock(EventFilter.class);
		when(filter1.accept(any(Event.class), eq(exchange))).thenReturn(true);
		when(filter2.accept(any(Event.class), eq(exchange))).thenReturn(true);
		processor = new DebeziumMessageProcessor(Arrays.asList(filter1, filter2));
		
		processor.process(exchange);
		
		assertNull(exchange.getProperty(EX_PROP_SKIP));
		verify(filter1).accept(any(Event.class), eq(exchange));
		verify(filter2).accept(any(Event.class), eq(exchange));
	}
	
	@Test
	public void process_shouldSetSkipPropertyToTrueIfFirstFilterReturnsFalse() throws Exception {
		Exchange exchange = WatcherTestUtils.createExchange("visit", 1, Snapshot.FALSE);
		EventFilter filter1 = Mockito.mock(EventFilter.class);
		EventFilter filter2 = Mockito.mock(EventFilter.class);
		when(filter1.accept(any(Event.class), eq(exchange))).thenReturn(false);
		processor = new DebeziumMessageProcessor(Arrays.asList(filter1, filter2));
		
		processor.process(exchange);
		
		assertTrue(exchange.getProperty(EX_PROP_SKIP, Boolean.class));
		verify(filter1).accept(any(Event.class), eq(exchange));
		verifyNoInteractions(filter2);
	}
	
	@Test
	public void process_shouldSetSkipPropertyToTrueIfAFilterInTheMiddleReturnsFalse() throws Exception {
		Exchange exchange = WatcherTestUtils.createExchange("visit", 1, Snapshot.FALSE);
		EventFilter filter1 = Mockito.mock(EventFilter.class);
		EventFilter filter2 = Mockito.mock(EventFilter.class);
		EventFilter filter3 = Mockito.mock(EventFilter.class);
		when(filter1.accept(any(Event.class), eq(exchange))).thenReturn(true);
		when(filter2.accept(any(Event.class), eq(exchange))).thenReturn(false);
		processor = new DebeziumMessageProcessor(Arrays.asList(filter1, filter2, filter3));
		
		processor.process(exchange);
		
		assertTrue(exchange.getProperty(EX_PROP_SKIP, Boolean.class));
		verify(filter1).accept(any(Event.class), eq(exchange));
		verify(filter2).accept(any(Event.class), eq(exchange));
		verifyNoInteractions(filter3);
	}
	
	@Test
	public void process_shouldSetSkipPropertyToTrueIfTheLastFilterReturnsFalse() throws Exception {
		Exchange exchange = WatcherTestUtils.createExchange("visit", 1, Snapshot.FALSE);
		EventFilter filter1 = Mockito.mock(EventFilter.class);
		EventFilter filter2 = Mockito.mock(EventFilter.class);
		EventFilter filter3 = Mockito.mock(EventFilter.class);
		when(filter1.accept(any(Event.class), eq(exchange))).thenReturn(true);
		when(filter2.accept(any(Event.class), eq(exchange))).thenReturn(true);
		when(filter3.accept(any(Event.class), eq(exchange))).thenReturn(false);
		processor = new DebeziumMessageProcessor(Arrays.asList(filter1, filter2, filter3));
		
		processor.process(exchange);
		
		assertTrue(exchange.getProperty(EX_PROP_SKIP, Boolean.class));
		verify(filter1).accept(any(Event.class), eq(exchange));
		verify(filter2).accept(any(Event.class), eq(exchange));
		verify(filter3).accept(any(Event.class), eq(exchange));
	}
	
}
