package org.openmrs.eip.app.sender;

import static java.util.Collections.synchronizedList;
import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.sender.SenderConstants.URI_DBZM_EVENT_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseParallelProcessor;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.DebeziumEvent;
import org.openmrs.eip.component.entity.Event;
import org.powermock.reflect.Whitebox;

public class BaseQueueProcessorTest {
	
	private DebeziumEventProcessor processor;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	private static ExecutorService executor;
	
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
	
	private DebeziumEventProcessor createProcessor(int threadCount) {
		processor = new DebeziumEventProcessor();
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
	public void process_shouldProcessAllSnapshotEventsInParallelForSlowThreads() throws Exception {
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
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		processor = createProcessor(size);
		
		processor.process(exchange);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			Assert.assertTrue(expectedResults.contains(event.getId()));
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
	}
	
	@Test
	public void process_shouldProcessAllSnapshotEventsInParallelForFastThreads() throws Exception {
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
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		processor = createProcessor(size);
		
		processor.process(exchange);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			Assert.assertTrue(expectedResults.contains(event.getId()));
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
	}
	
	@Test
	public void process_shouldProcessAllNonSnapshotEventsInSerialInCurrentThread() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 10;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = new ArrayList(size);
		List<String> threadNames = new ArrayList(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, false);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				threadNames.add(Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		processor = createProcessor(size);
		
		processor.process(exchange);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, threadNames.size());
		
		for (int i = 0; i < size; i++) {
			Assert.assertEquals(Long.valueOf(i), expectedResults.get(i));
			DebeziumEvent event = events.get(i);
			String threadName = threadNames.get(i);
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(processor.getThreadName(event), threadName.split(":")[2]);
		}
	}
	
	@Test
	public void process_shouldProcessAMixOfSnapshotAndNonSnapshotEventsAndMaintainTheIndicesOfNonSnapshots()
	    throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		final int nonSnapshotMsgIndex15 = 15;
		final int nonSnapshotMsgIndex23 = 23;
		final int nonSnapshotMsgIndex24 = 24;
		final int nonSnapshotMsgIndex38 = 25;
		final int nonSnapshotMsgIndex49 = 98;
		List<Integer> nonSnapshotMsgIndices = new ArrayList();
		nonSnapshotMsgIndices.add(nonSnapshotMsgIndex15);
		nonSnapshotMsgIndices.add(nonSnapshotMsgIndex23);
		nonSnapshotMsgIndices.add(nonSnapshotMsgIndex24);
		nonSnapshotMsgIndices.add(nonSnapshotMsgIndex38);
		nonSnapshotMsgIndices.add(nonSnapshotMsgIndex49);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, nonSnapshotMsgIndices.contains(i) ? false : true);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		processor = createProcessor(size);
		
		processor.process(exchange);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			Assert.assertTrue(expectedResults.contains(event.getId()));
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
		
		//Non-snapshot events are only processed after all snapshot events ahead of the so the order which they are
		//processed is preserved and should have been processed in the current thread
		for (Integer i : nonSnapshotMsgIndices) {
			DebeziumEvent event = events.get(i);
			String threadName = expectedMsgIdThreadNameMap.get(event.getId());
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
	}
	
	@Test
	public void process_shouldProcessAMixOfEventsWithAllSnapshotAtTheStartOfTheQueue() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 50;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size / 2; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		List<Integer> nonSnapshotMsgIndices = new ArrayList();
		for (int i = (size / 2); i < size; i++) {
			nonSnapshotMsgIndices.add(i);
			DebeziumEvent m = createDebeziumEvent(i, false);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		processor = createProcessor(size);
		
		processor.process(exchange);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		assertEquals(size / 2, nonSnapshotMsgIndices.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			Assert.assertTrue(expectedResults.contains(event.getId()));
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
		
		//Non-snapshot events are only processed after all snapshot events ahead of the so the order which they are
		//processed is preserved and should have been processed in the current thread
		for (Integer i : nonSnapshotMsgIndices) {
			DebeziumEvent event = events.get(i);
			String threadName = expectedMsgIdThreadNameMap.get(event.getId());
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
		
		//Snapshots should all have been first synced before incremental events
		for (int i = 0; i < size / 2; i++) {
			Assert.assertTrue(events.get(expectedResults.get(i).intValue()).getEvent().getSnapshot());
		}
		
		for (int i = (size / 2); i < size; i++) {
			Assert.assertFalse(events.get(expectedResults.get(i).intValue()).getEvent().getSnapshot());
		}
	}
	
	@Test
	public void process_shouldProcessAMixOfEventsWithAllSnapshotAtTheEndOfTheQueue() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 50;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		List<Integer> nonSnapshotMsgIndices = new ArrayList();
		for (int i = 0; i < size / 2; i++) {
			nonSnapshotMsgIndices.add(i);
			DebeziumEvent m = createDebeziumEvent(i, false);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		for (int i = (size / 2); i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			events.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		processor = createProcessor(size);
		
		processor.process(exchange);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		assertEquals(size / 2, nonSnapshotMsgIndices.size());
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent event = events.get(i);
			Assert.assertTrue(expectedResults.contains(event.getId()));
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
		
		//Non-snapshot events are only processed after all snapshot events ahead of the so the order which they are
		//processed is preserved and should have been processed in the current thread
		for (Integer i : nonSnapshotMsgIndices) {
			DebeziumEvent event = events.get(i);
			String threadName = expectedMsgIdThreadNameMap.get(event.getId());
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
		
		//Incremental events should all have been first synced before snapshot
		for (int i = 0; i < size / 2; i++) {
			Assert.assertFalse(events.get(expectedResults.get(i).intValue()).getEvent().getSnapshot());
		}
		
		for (int i = (size / 2); i < size; i++) {
			Assert.assertTrue(events.get(expectedResults.get(i).intValue()).getEvent().getSnapshot());
		}
	}
	
	@Test
	public void process_shouldProcessSnapshotEventsInSerialForTheSameRow() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 5;
		List<DebeziumEvent> events = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			m.getEvent().setPrimaryKeyId("same-id");
			m.getEvent().setIdentifier("same-uuid");
			events.add(m);
			Mockito.doAnswer(invocation -> {
				DebeziumEvent arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(URI_DBZM_EVENT_PROCESSOR, m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		processor = createProcessor(size);
		
		processor.process(exchange);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		DebeziumEvent firstMsg = events.get(0);
		Assert.assertTrue(expectedResults.contains(firstMsg.getId()));
		assertEquals(processor.getThreadName(firstMsg), expectedMsgIdThreadNameMap.get(firstMsg.getId()).split(":")[2]);
		
		//All other events for the same row are only processed in serial after first snapshot events is encountered
		for (int i = 1; i < size; i++) {
			DebeziumEvent event = events.get(i);
			String threadName = expectedMsgIdThreadNameMap.get(event.getId());
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(processor.getThreadName(event), expectedMsgIdThreadNameMap.get(event.getId()).split(":")[2]);
		}
	}
	
	@Test
	public void process_shouldNotProcessEventsWhenTheApplicationContextIsStopping() throws Exception {
		AppUtils.setAppContextStopping();
		final int size = 2;
		List<DebeziumEvent> events = new ArrayList(size);
		
		for (int i = 0; i < size; i++) {
			DebeziumEvent m = createDebeziumEvent(i, true);
			events.add(m);
		}
		
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(events);
		DebeziumEventProcessor processor = createProcessor(size);
		
		processor.process(exchange);
		
		Mockito.verifyNoInteractions(mockProducerTemplate);
	}
	
}
