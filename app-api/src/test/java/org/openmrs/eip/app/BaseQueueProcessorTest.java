package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.THREAD_THRESHOLD_MULTIPLIER;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.utils.Utils;

public class BaseQueueProcessorTest {
	
	private static final String MOCK_URI = "mock:uri";
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	private ThreadPoolExecutor executor;
	
	public class TestEventProcessor extends BaseFromCamelToCamelEndpointProcessor<DebeziumEvent> {
		
		public TestEventProcessor(ProducerTemplate producerTemplate, ThreadPoolExecutor executor) {
			super(MOCK_URI, producerTemplate, executor);
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
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		setInternalState(AppUtils.class, "appContextStopping", false);
		setInternalState(BaseQueueProcessor.class, "initialized", false);
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(SyncConstants.DEFAULT_THREAD_NUMBER);
	}
	
	private BaseQueueProcessor createProcessor() {
		BaseQueueProcessor processor = new TestEventProcessor(mockProducerTemplate, executor);
		setInternalState(processor, ProducerTemplate.class, mockProducerTemplate);
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
		
		BaseQueueProcessor processor = createProcessor();
		
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
		
		BaseQueueProcessor processor = createProcessor();
		
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
		
		BaseQueueProcessor processor = createProcessor();
		
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
		
		BaseQueueProcessor processor = createProcessor();
		
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
		
		createProcessor().processWork(events);
		
		Mockito.verifyNoInteractions(mockProducerTemplate);
	}
	
	@Test
	public void initIfNecessary_shouldInitializeStaticFields() {
		BaseQueueProcessor processor = createProcessor();
		setInternalState(BaseQueueProcessor.class, "initialized", false);
		setInternalState(BaseQueueProcessor.class, "taskThreshold", 0);
		Assert.assertFalse(getInternalState(BaseQueueProcessor.class, "initialized"));
		assertEquals(0, ((Integer) getInternalState(BaseQueueProcessor.class, "taskThreshold")).intValue());
		
		processor.initIfNecessary();
		
		Assert.assertTrue(getInternalState(BaseQueueProcessor.class, "initialized"));
		final int expected = executor.getMaximumPoolSize() * THREAD_THRESHOLD_MULTIPLIER;
		assertEquals(expected, ((Integer) getInternalState(BaseQueueProcessor.class, "taskThreshold")).intValue());
	}
	
	@Test
	public void initIfNecessary_shouldSkipIfAlreadyInitialized() {
		BaseQueueProcessor processor = createProcessor();
		setInternalState(BaseQueueProcessor.class, "taskThreshold", 0);
		assertEquals(true, getInternalState(BaseQueueProcessor.class, "initialized"));
		assertEquals(0, ((Integer) getInternalState(BaseQueueProcessor.class, "taskThreshold")).intValue());
		
		processor.initIfNecessary();
		assertEquals(true, getInternalState(BaseQueueProcessor.class, "initialized"));
		assertEquals(0, ((Integer) getInternalState(BaseQueueProcessor.class, "taskThreshold")).intValue());
	}
	
}
