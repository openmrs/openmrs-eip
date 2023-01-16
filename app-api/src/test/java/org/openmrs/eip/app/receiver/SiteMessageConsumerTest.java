package org.openmrs.eip.app.receiver;

import static java.util.Collections.synchronizedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.model.DrugOrderModel;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.TestOrderModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReceiverContext.class, CamelUtils.class })
public class SiteMessageConsumerTest {
	
	private SiteMessageConsumer consumer;
	
	private ExecutorService executor;
	
	private SiteInfo siteInfo;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private ExtendedCamelContext mockCamelContext;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverContext.class);
		PowerMockito.mockStatic(CamelUtils.class);
		siteInfo = new SiteInfo();
		siteInfo.setIdentifier("testSite");
		Mockito.when(mockProducerTemplate.getCamelContext()).thenReturn(mockCamelContext);
	}
	
	private void initExecutorAndConsumer(final int size) {
		ReceiverActiveMqMessagePublisher messagePublisher = new ReceiverActiveMqMessagePublisher();
		Whitebox.setInternalState(messagePublisher, "endpointConfig", "queue.name.prefix.{0}");
		Whitebox.setInternalState(messagePublisher, ProducerTemplate.class, mockProducerTemplate);
		
		executor = Executors.newFixedThreadPool(size);
		consumer = new SiteMessageConsumer(URI_MSG_PROCESSOR, siteInfo, size, executor);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, mockProducerTemplate);
		Whitebox.setInternalState(consumer, ReceiverActiveMqMessagePublisher.class, messagePublisher);
	}
	
	private SyncMessage createMessage(int index, boolean snapshot) {
		SyncMessage m = new SyncMessage();
		m.setId(Long.valueOf(index));
		m.setModelClassName(PersonModel.class.getName());
		m.setIdentifier("msg" + index);
		m.setSite(siteInfo);
		m.setSnapshot(snapshot);
		return m;
	}
	
	@Test
	public void processMessages_shouldProcessAllSnapshotMessagesInParallelForSlowThreads() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Thread.sleep(500);
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertTrue(expectedResults.contains(msg.getId()));
			assertNotEquals(originalThread, expectedMsgIdThreadMap.get(msg.getId()));
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
	}
	
	@Test
	public void processMessages_shouldProcessAllSnapshotMessagesInParallelForFastThreads() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertTrue(expectedResults.contains(msg.getId()));
			assertNotEquals(originalThread, expectedMsgIdThreadMap.get(msg.getId()));
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
	}
	
	@Test
	public void processMessages_shouldProcessAllNonSnapshotMessagesInParallel() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 10;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, false);
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Thread.sleep(500);
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertTrue(expectedResults.contains(msg.getId()));
			assertNotEquals(originalThread, expectedMsgIdThreadMap.get(msg.getId()));
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
	}
	
	@Test
	public void processMessages_shouldProcessOnlyTheFirstMessageForAnEntityAndSkipTheOthersForTheSameEntity()
	    throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 20;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		List<SyncMessage> sameEntityMessages = new ArrayList();
		final int multiplesOf = 4;
		final int expectedProcessedMsgSize = 17;
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, false);
			if (i > 0 && i % multiplesOf == 0) {
				m.setIdentifier("same-uuid");
				sameEntityMessages.add(m);
			}
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(expectedProcessedMsgSize, expectedResults.size());
		assertEquals(expectedProcessedMsgSize, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			//All other messages for the same entity are skipped after first for the entity is encountered
			if (i > multiplesOf && i % multiplesOf == 0) {
				assertFalse(expectedResults.contains(msg.getId()));
				assertFalse(expectedMsgIdThreadMap.containsKey(msg.getId()));
				assertFalse(expectedMsgIdThreadNameMap.containsKey(msg.getId()));
			} else {
				assertTrue(expectedResults.contains(msg.getId()));
				assertNotNull(expectedMsgIdThreadMap.get(msg.getId()));
				assertNotEquals(originalThread, expectedMsgIdThreadMap.get(msg.getId()));
				assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
			}
		}
	}
	
	@Test
	public void processMessages_shouldSkipMessagesForTheSamePatientIfPrecededByPersonMessages() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 3;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			m.setIdentifier("same-uuid");
			m.setModelClassName(i == 0 ? PersonModel.class.getName() : PatientModel.class.getName());
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(1, expectedResults.size());
		assertEquals(1, expectedMsgIdThreadNameMap.size());
		
		SyncMessage firstMsg = messages.get(0);
		assertTrue(expectedResults.contains(firstMsg.getId()));
		assertNotNull(expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertNotEquals(originalThread, expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertEquals(consumer.getThreadName(firstMsg), expectedMsgIdThreadNameMap.get(firstMsg.getId()).split(":")[1]);
		
		//All other subclass messages for the same entity are skipped after first for the entity is encountered
		for (int i = 1; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertFalse(expectedResults.contains(msg.getId()));
			assertFalse(expectedMsgIdThreadMap.containsKey(msg.getId()));
			assertFalse(expectedMsgIdThreadNameMap.containsKey(msg.getId()));
		}
	}
	
	@Test
	public void processMessages_shouldSkipMessagesForTheSamePersonIfPrecededByPatientMessages() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 3;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			m.setIdentifier("same-uuid");
			m.setModelClassName(i == 0 ? PatientModel.class.getName() : PersonModel.class.getName());
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(1, expectedResults.size());
		assertEquals(1, expectedMsgIdThreadNameMap.size());
		
		SyncMessage firstMsg = messages.get(0);
		assertTrue(expectedResults.contains(firstMsg.getId()));
		assertNotNull(expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertNotEquals(originalThread, expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertEquals(consumer.getThreadName(firstMsg), expectedMsgIdThreadNameMap.get(firstMsg.getId()).split(":")[1]);
		
		//All other subclass messages for the same entity are skipped after first for the entity is encountered
		for (int i = 1; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertFalse(expectedResults.contains(msg.getId()));
			assertFalse(expectedMsgIdThreadMap.containsKey(msg.getId()));
			assertFalse(expectedMsgIdThreadNameMap.containsKey(msg.getId()));
		}
	}
	
	@Test
	public void processMessages_shouldSkipMessagesForTheSameTestOrderIfPrecededByOrderMessages() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 3;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			m.setIdentifier("same-uuid");
			m.setModelClassName(i == 0 ? OrderModel.class.getName() : TestOrderModel.class.getName());
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(1, expectedResults.size());
		assertEquals(1, expectedMsgIdThreadNameMap.size());
		
		SyncMessage firstMsg = messages.get(0);
		assertTrue(expectedResults.contains(firstMsg.getId()));
		assertNotNull(expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertNotEquals(originalThread, expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertEquals(consumer.getThreadName(firstMsg), expectedMsgIdThreadNameMap.get(firstMsg.getId()).split(":")[1]);
		
		//All other subclass messages for the same entity are skipped after first for the entity is encountered
		for (int i = 1; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertFalse(expectedResults.contains(msg.getId()));
			assertFalse(expectedMsgIdThreadMap.containsKey(msg.getId()));
			assertFalse(expectedMsgIdThreadNameMap.containsKey(msg.getId()));
		}
	}
	
	@Test
	public void processMessages_shouldSkipMessagesForTheSameOrderIfPrecededByTestOrderMessages() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 3;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			m.setIdentifier("same-uuid");
			m.setModelClassName(i == 0 ? TestOrderModel.class.getName() : OrderModel.class.getName());
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(1, expectedResults.size());
		assertEquals(1, expectedMsgIdThreadNameMap.size());
		
		SyncMessage firstMsg = messages.get(0);
		assertTrue(expectedResults.contains(firstMsg.getId()));
		assertNotNull(expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertNotEquals(originalThread, expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertEquals(consumer.getThreadName(firstMsg), expectedMsgIdThreadNameMap.get(firstMsg.getId()).split(":")[1]);
		
		//All other subclass messages for the same entity are skipped after first for the entity is encountered
		for (int i = 1; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertFalse(expectedResults.contains(msg.getId()));
			assertFalse(expectedMsgIdThreadMap.containsKey(msg.getId()));
			assertFalse(expectedMsgIdThreadNameMap.containsKey(msg.getId()));
		}
	}
	
	@Test
	public void processMessages_shouldSkipMessagesForTheSameDrugOrderIfPrecededByOrderMessages() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 3;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			m.setIdentifier("same-uuid");
			m.setModelClassName(i == 0 ? OrderModel.class.getName() : DrugOrderModel.class.getName());
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(1, expectedResults.size());
		assertEquals(1, expectedMsgIdThreadNameMap.size());
		
		SyncMessage firstMsg = messages.get(0);
		assertTrue(expectedResults.contains(firstMsg.getId()));
		assertNotNull(expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertNotEquals(originalThread, expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertEquals(consumer.getThreadName(firstMsg), expectedMsgIdThreadNameMap.get(firstMsg.getId()).split(":")[1]);
		
		//All other subclass messages for the same entity are skipped after first for the entity is encountered
		for (int i = 1; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertFalse(expectedResults.contains(msg.getId()));
			assertFalse(expectedMsgIdThreadMap.containsKey(msg.getId()));
			assertFalse(expectedMsgIdThreadNameMap.containsKey(msg.getId()));
		}
	}
	
	@Test
	public void processMessages_shouldSkipMessagesForTheSameOrderIfPrecededByDrugOrderMessages() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 3;
		initExecutorAndConsumer(size);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, Thread> expectedMsgIdThreadMap = new ConcurrentHashMap(size);
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			m.setIdentifier("same-uuid");
			m.setModelClassName(i == 0 ? DrugOrderModel.class.getName() : OrderModel.class.getName());
			messages.add(m);
			Mockito.when(CamelUtils.send(eq(URI_MSG_PROCESSOR), any(Exchange.class))).thenAnswer(invocation -> {
				Exchange exchange = invocation.getArgument(1);
				SyncMessage arg = exchange.getIn().getBody(SyncMessage.class);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadMap.put(arg.getId(), Thread.currentThread());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
				return null;
			});
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(1, expectedResults.size());
		assertEquals(1, expectedMsgIdThreadNameMap.size());
		
		SyncMessage firstMsg = messages.get(0);
		assertTrue(expectedResults.contains(firstMsg.getId()));
		assertNotNull(expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertNotEquals(originalThread, expectedMsgIdThreadMap.get(firstMsg.getId()));
		assertEquals(consumer.getThreadName(firstMsg), expectedMsgIdThreadNameMap.get(firstMsg.getId()).split(":")[1]);
		
		//All other subclass messages for the same entity are skipped after first for the entity is encountered
		for (int i = 1; i < size; i++) {
			SyncMessage msg = messages.get(i);
			assertFalse(expectedResults.contains(msg.getId()));
			assertFalse(expectedMsgIdThreadMap.containsKey(msg.getId()));
			assertFalse(expectedMsgIdThreadNameMap.containsKey(msg.getId()));
		}
	}
	
}
