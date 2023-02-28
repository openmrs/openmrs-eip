package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.reflect.Whitebox;

public class BaseQueueProcessorTest {
	
	private static final String MOCK_URI = "mock:uri";
	
	private BaseQueueProcessor processor;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	private static ExecutorService executor;
	
	public class TestEventProcessor extends BaseFromCamelToCamelEndpointProcessor<DebeziumEvent> {
		
		public TestEventProcessor(ProducerTemplate producerTemplate) {
			super(MOCK_URI, producerTemplate);
		}
		
		@Override
		public String getProcessorName() {
			return "test proc";
		}
		
		@Override
		public String getUniqueId(DebeziumEvent item) {
			return item.getEvent().getPrimaryKeyId();
		}
		
		@Override
		public String getQueueName() {
			return "test-proc";
		}
		
		@Override
		public String getThreadName(DebeziumEvent event) {
			String name = event.getEvent().getTableName() + "-" + event.getEvent().getPrimaryKeyId() + "-" + event.getId();
			if (StringUtils.isNotBlank(event.getEvent().getIdentifier())) {
				name += ("-" + event.getEvent().getIdentifier());
			}
			
			return name;
		}
		
		@Override
		public String getLogicalType(DebeziumEvent item) {
			return item.getEvent().getTableName();
		}
		
		@Override
		public List<String> getLogicalTypeHierarchy(String logicalTypeName) {
			return Utils.getListOfTablesInHierarchy(logicalTypeName);
		}
		
	}
	
	@BeforeClass
	public static void beforeClass() {
		executor = Executors.newFixedThreadPool(SyncConstants.DEFAULT_THREAD_NUMBER);
		Whitebox.setInternalState(BaseParallelProcessor.class, "executor", executor);
	}
	
	@AfterClass
	public static void afterClass() {
		executor.shutdownNow();
		Whitebox.setInternalState(BaseParallelProcessor.class, "executor", (Object) null);
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(AppUtils.class, "appContextStopping", false);
	}
	
	private BaseQueueProcessor createProcessor(int threadCount) {
		processor = new TestEventProcessor(mockProducerTemplate);
		Whitebox.setInternalState(processor, int.class, threadCount);
		Whitebox.setInternalState(processor, ProducerTemplate.class, mockProducerTemplate);
		return processor;
	}
	
	private DebeziumEvent createDebeziumEvent(int index, boolean snapshot) {
		Event e = new Event();
		e.setTableName("person");
		e.setPrimaryKeyId("event" + index);
		e.setIdentifier("event" + index);
		e.setSnapshot(snapshot);
		DebeziumEvent dbzmEvent = new DebeziumEvent();
		dbzmEvent.setId(Long.valueOf(index));
		dbzmEvent.setEvent(e);
		return dbzmEvent;
	}
	
	@Test
	public void processWork_shouldProcessAllEventsInParallelForSlowThreads() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(MOCK_URI, m);
		}
		
		processor = createProcessor(size);
		
		processor.processWork(events);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			assertTrue(expectedResults.contains(event.getId()));
			assertNotEquals(originalThreadName, expectedMsgIdThreadNameMap.get(event.getId()).split(":")[0]);
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
	}
	
	@Test
	public void processWork_shouldProcessAllEventsInParallelForFastThreads() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(MOCK_URI, m);
		}
		
		processor = createProcessor(size);
		
		processor.processWork(events);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			assertTrue(expectedResults.contains(event.getId()));
			assertNotEquals(originalThreadName, expectedMsgIdThreadNameMap.get(event.getId()).split(":")[0]);
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
	}
	
	@Test
	public void processWork_shouldProcessOneItemInCaseOfMultipleItemsWithTheSameKey() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 20;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			if (i % 4 == 0) {
				m.getEvent().setPrimaryKeyId("same-id");
				m.getEvent().setIdentifier("same-uuid");
			}
			events.add(m);
			Mockito.doAnswer(invocation -> {
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(MOCK_URI, m);
		}
		
		processor = createProcessor(size);
		
		processor.processWork(events);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(16, expectedResults.size());
		assertEquals(16, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			if (i > 0 && i % 4 == 0) {
				assertFalse(expectedResults.contains(event.getId()));
			} else {
				assertTrue(expectedResults.contains(event.getId()));
				assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
				String threadName = expectedMsgIdThreadNameMap.get(event.getId()).split(":")[0];
				assertNotEquals(originalThreadName, threadName);
			}
		}
	}
	
	@Test
	public void processWork_shouldProcessOneItemInCaseOfMultipleItemsWithTheSameKeyFromDifferentTablesButSameHierarchy()
	    throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 20;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			if (i % 4 == 0) {
				m.getEvent().setPrimaryKeyId("same-id");
				m.getEvent().setIdentifier("same-uuid");
				if (i == 0) {
					m.getEvent().setTableName("person");
				} else {
					m.getEvent().setTableName("patient");
				}
			}
			events.add(m);
			Mockito.doAnswer(invocation -> {
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(MOCK_URI, m);
		}
		
		processor = createProcessor(size);
		
		processor.processWork(events);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(16, expectedResults.size());
		assertEquals(16, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			if (i > 0 && i % 4 == 0) {
				assertFalse(expectedResults.contains(event.getId()));
			} else {
				assertTrue(expectedResults.contains(event.getId()));
				assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
				String threadName = expectedMsgIdThreadNameMap.get(event.getId()).split(":")[0];
				assertNotEquals(originalThreadName, threadName);
			}
		}
	}
	
	@Test
	public void processWork_shouldNotProcessEventsWhenTheApplicationContextIsStopping() throws Exception {
		AppUtils.setAppContextStopping();
		final int size = 2;
		List<DebeziumEvent> events = new ArrayList(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			events.add(m);
		}
		
		createProcessor(size).processWork(events);
		
		Mockito.verifyNoInteractions(mockProducerTemplate);
	}
	
}
