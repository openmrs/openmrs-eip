package org.openmrs.eip.app;

import static java.util.Collections.singletonList;
import static java.util.Collections.synchronizedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.ROUTE_URI_CHANGE_EVNT_PROCESSOR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

public class ChangeEventProcessorTest {
	
	private static final String TABLE = "person";
	
	private ChangeEventProcessor processor;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	private ChangeEventProcessor createProcessor(int threadCount) {
		processor = new ChangeEventProcessor();
		Whitebox.setInternalState(processor, int.class, threadCount);
		Whitebox.setInternalState(processor, ProducerTemplate.class, mockProducerTemplate);
		return processor;
	}
	
	private Exchange createExchange(int index, String snapshot) {
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		final Field id = new Field("person_id", 0, new ConnectSchema(Schema.Type.INT32));
		final List<Field> ids = singletonList(id);
		//ids.add(id);
		final Struct primaryKey = new Struct(
		        new ConnectSchema(Schema.Type.STRUCT, false, null, "key", null, null, null, ids, null, null));
		primaryKey.put("person_id", index);
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put("table", TABLE);
		sourceMetadata.put("snapshot", snapshot);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		return exchange;
	}
	
	private Integer getId(Exchange e) {
		Struct primaryKeyStruct = e.getMessage().getHeader(DebeziumConstants.HEADER_KEY, Struct.class);
		return primaryKeyStruct.getInt32(primaryKeyStruct.schema().fields().get(0).name());
	}
	
	@Test
	public void process_shouldProcessAllSnapshotEventsInParallelForSlowThreads() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		List<Exchange> exchanges = new ArrayList(size);
		List<Integer> expectedResults = synchronizedList(new ArrayList(size));
		Map<Integer, String> expectedIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			Exchange e = createExchange(i, i < size - 1 ? Boolean.TRUE.toString() : "last");
			exchanges.add(e);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				Exchange arg = invocation.getArgument(1);
				Integer id = getId(arg);
				expectedResults.add(id);
				expectedIdThreadNameMap.put(id, Thread.currentThread().getName());
				assertTrue(CustomFileOffsetBackingStore.isPaused());
				return null;
			}).when(mockProducerTemplate).send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, e);
		}
		
		processor = createProcessor(size);
		for (Exchange exchange : exchanges) {
			processor.process(exchange);
		}
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedIdThreadNameMap.size());
		assertFalse(CustomFileOffsetBackingStore.isPaused());
		
		for (int i = 0; i < size; i++) {
			Integer id = getId(exchanges.get(i));
			assertTrue(expectedResults.contains(id));
			assertEquals(processor.getThreadName(TABLE, id.toString()), expectedIdThreadNameMap.get(id).split(":")[2]);
		}
	}
	
	@Test
	public void process_shouldProcessAllSnapshotEventsInParallelForFastThreads() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		List<Exchange> exchanges = new ArrayList(size);
		List<Integer> expectedResults = synchronizedList(new ArrayList(size));
		Map<Integer, String> expectedIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			Exchange e = createExchange(i, i < size - 1 ? Boolean.TRUE.toString() : "last");
			exchanges.add(e);
			Mockito.doAnswer(invocation -> {
				Exchange arg = invocation.getArgument(1);
				Integer id = getId(arg);
				expectedResults.add(id);
				expectedIdThreadNameMap.put(id, Thread.currentThread().getName());
				assertTrue(CustomFileOffsetBackingStore.isPaused());
				return null;
			}).when(mockProducerTemplate).send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, e);
		}
		
		processor = createProcessor(size);
		for (Exchange exchange : exchanges) {
			processor.process(exchange);
		}
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedIdThreadNameMap.size());
		assertFalse(CustomFileOffsetBackingStore.isPaused());
		
		for (int i = 0; i < size; i++) {
			Integer id = getId(exchanges.get(i));
			assertTrue(expectedResults.contains(id));
			assertEquals(processor.getThreadName(TABLE, id.toString()), expectedIdThreadNameMap.get(id).split(":")[2]);
		}
	}
	
	@Test
	public void process_shouldProcessAllNonSnapshotEventsInSerialInCurrentThread() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 10;
		List<Exchange> exchanges = new ArrayList(size);
		List<Integer> expectedResults = new ArrayList(size);
		List<String> threadNames = new ArrayList(size);
		
		for (int i = 0; i < size; i++) {
			Exchange e = createExchange(i, Boolean.FALSE.toString());
			exchanges.add(e);
			Mockito.doAnswer(invocation -> {
				Exchange arg = invocation.getArgument(1);
				Integer id = getId(arg);
				expectedResults.add(id);
				threadNames.add(Thread.currentThread().getName());
				assertFalse(CustomFileOffsetBackingStore.isPaused());
				return null;
			}).when(mockProducerTemplate).send(ROUTE_URI_CHANGE_EVNT_PROCESSOR, e);
		}
		
		processor = createProcessor(size);
		for (Exchange exchange : exchanges) {
			processor.process(exchange);
		}
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, threadNames.size());
		assertFalse(CustomFileOffsetBackingStore.isPaused());
		
		for (int i = 0; i < size; i++) {
			assertEquals(i, expectedResults.get(i).intValue());
			String threadName = threadNames.get(i);
			Integer id = getId(exchanges.get(i));
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(processor.getThreadName(TABLE, id.toString()), threadName.split(":")[2]);
		}
	}
	
}
