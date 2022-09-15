package org.openmrs.eip.app.sender;

import static java.util.Collections.singletonList;
import static org.apache.kafka.connect.data.Schema.Type.STRUCT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema.Type;
import org.apache.kafka.connect.data.Struct;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.openmrs.eip.app.CustomFileOffsetBackingStore;
import org.openmrs.eip.app.management.repository.DebeziumEventRepository;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CustomFileOffsetBackingStore.class)
public class ChangeEventHandlerTest {
	
	@Mock
	private DebeziumEventRepository mockRepository;
	
	@Mock
	private Logger mockLogger;
	
	private ChangeEventHandler handler;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(CustomFileOffsetBackingStore.class);
		handler = new ChangeEventHandler(mockRepository);
		Whitebox.setInternalState(ChangeEventHandler.class, Logger.class, mockLogger);
	}
	
	private Exchange createExchange(int index, String snapshot, String table) {
		return null;
	}
	
	@Test
	public void handle_shouldFailForAnUnknownDatabaseOperation() {
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		final String op = "k";
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		exchange.setMessage(message);
		expectedException.expect(EIPException.class);
		expectedException.expectMessage(equalTo("Don't know how to handle DB event with operation: " + op));
		
		handler.handle(null, null, true, null, exchange);
	}
	
	@Test
	public void handle_shouldSkipASubclassTableSnapshotEvent() {
		when(mockLogger.isTraceEnabled()).thenReturn(true);
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, "c");
		exchange.setMessage(message);
		final String tableName = "patient";
		
		handler.handle(tableName, null, true, null, exchange);
		
		verify(mockLogger).trace("Skipping " + tableName + " snapshot event");
		verifyNoInteractions(mockRepository);
	}
	
	@Test
	public void handle_shouldProcessASnapshotEvent() {
		final String tableName = "person";
		final String id = "1";
		final String uuid = "test-uuid";
		final String op = "c";
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		Field uuidField = new Field("uuid", 0, new ConnectSchema(Type.STRING));
		List<Field> afterFields = singletonList(uuidField);
		Struct afterState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "after", null, null, null, afterFields, null, null));
		afterState.put(uuidField, uuid);
		message.setBody(afterState);
		exchange.setMessage(message);
		
		handler.handle(tableName, id, true, null, exchange);
		
		verify(mockRepository).save(argThat(dbzmEvent -> {
			try {
				return tableName.equals(dbzmEvent.getEvent().getTableName())
				        && id.equals(dbzmEvent.getEvent().getPrimaryKeyId())
				        && uuid.equals(dbzmEvent.getEvent().getIdentifier())
				        && op.equals(dbzmEvent.getEvent().getOperation()) && dbzmEvent.getEvent().getSnapshot()
				        && dbzmEvent.getDateCreated() != null;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
	}
	
	@Test
	public void handle_shouldProcessAnInsertEvent() {
		final String tableName = "person";
		final String id = "1";
		final String uuid = "test-uuid";
		final String op = "c";
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		Field uuidField = new Field("uuid", 0, new ConnectSchema(Type.STRING));
		List<Field> afterFields = singletonList(uuidField);
		Struct afterState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "after", null, null, null, afterFields, null, null));
		afterState.put(uuidField, uuid);
		message.setBody(afterState);
		exchange.setMessage(message);
		
		handler.handle(tableName, id, false, null, exchange);
		
		verify(mockRepository).save(argThat(dbzmEvent -> {
			try {
				return tableName.equals(dbzmEvent.getEvent().getTableName())
				        && id.equals(dbzmEvent.getEvent().getPrimaryKeyId())
				        && uuid.equals(dbzmEvent.getEvent().getIdentifier())
				        && op.equals(dbzmEvent.getEvent().getOperation()) && !dbzmEvent.getEvent().getSnapshot()
				        && dbzmEvent.getDateCreated() != null;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
	}
	
	@Test
	public void handle_shouldProcessAnUpdateEvent() {
		final String tableName = "person";
		final String id = "1";
		final String uuid = "test-uuid";
		final String op = "u";
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		Field uuidField = new Field("uuid", 0, new ConnectSchema(Type.STRING));
		List<Field> afterFields = singletonList(uuidField);
		Struct afterState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "after", null, null, null, afterFields, null, null));
		afterState.put(uuidField, uuid);
		message.setBody(afterState);
		exchange.setMessage(message);
		
		handler.handle(tableName, id, false, null, exchange);
		
		verify(mockRepository).save(argThat(dbzmEvent -> {
			try {
				return tableName.equals(dbzmEvent.getEvent().getTableName())
				        && id.equals(dbzmEvent.getEvent().getPrimaryKeyId())
				        && uuid.equals(dbzmEvent.getEvent().getIdentifier())
				        && op.equals(dbzmEvent.getEvent().getOperation()) && !dbzmEvent.getEvent().getSnapshot()
				        && dbzmEvent.getDateCreated() != null;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
	}
	
	@Test
	public void handle_shouldProcessADeleteEvent() {
		final String tableName = "person";
		final String id = "1";
		final String uuid = "test-uuid";
		final String op = "d";
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		Field uuidField = new Field("uuid", 0, new ConnectSchema(Type.STRING));
		List<Field> beforeFields = singletonList(uuidField);
		Struct beforeState = new Struct(
		        new ConnectSchema(STRUCT, false, null, "before", null, null, null, beforeFields, null, null));
		beforeState.put(uuidField, uuid);
		message.setHeader(DebeziumConstants.HEADER_BEFORE, beforeState);
		exchange.setMessage(message);
		
		handler.handle(tableName, id, false, null, exchange);
		
		verify(mockRepository).save(argThat(dbzmEvent -> {
			try {
				return tableName.equals(dbzmEvent.getEvent().getTableName())
				        && id.equals(dbzmEvent.getEvent().getPrimaryKeyId())
				        && uuid.equals(dbzmEvent.getEvent().getIdentifier())
				        && op.equals(dbzmEvent.getEvent().getOperation()) && !dbzmEvent.getEvent().getSnapshot()
				        && dbzmEvent.getDateCreated() != null;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
	}
	
	@Test
	public void handle_shouldNotSetIdentifierForASubclassTableEVent() {
		final String tableName = "patient";
		final String id = "1";
		final String op = "c";
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		message.setHeader(DebeziumConstants.HEADER_OPERATION, op);
		exchange.setMessage(message);
		
		handler.handle(tableName, id, false, null, exchange);
		
		verify(mockRepository).save(argThat(dbzmEvent -> {
			try {
				return tableName.equals(dbzmEvent.getEvent().getTableName())
				        && id.equals(dbzmEvent.getEvent().getPrimaryKeyId())
				        && op.equals(dbzmEvent.getEvent().getOperation()) && !dbzmEvent.getEvent().getSnapshot()
				        && dbzmEvent.getEvent().getIdentifier() == null && dbzmEvent.getDateCreated() != null;
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));
	}
	
}
