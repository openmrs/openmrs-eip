package org.openmrs.eip.app.receiver;

import static java.util.Collections.synchronizedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.openmrs.eip.app.SyncConstants.THREAD_THRESHOLD_MULTIPLIER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.DEFAULT_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_CONFLICT_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_ERROR_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_MSG_PROCESSOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;
import static org.openmrs.eip.app.receiver.SiteMessageConsumer.ENTITY;
import static org.openmrs.eip.app.receiver.SiteMessageConsumer.JPA_URI_PREFIX;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.ProducerTemplate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.DrugOrderModel;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.TestOrderModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, ReceiverContext.class, CamelUtils.class })
public class SiteMessageConsumerTest {
	
	private static final String MOCK_PROCESSOR_URI = "mock:" + ROUTE_ID_MSG_PROCESSOR;
	
	private SiteMessageConsumer consumer;
	
	private ThreadPoolExecutor executor;
	
	private SiteInfo siteInfo;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private ExtendedCamelContext mockCamelContext;
	
	@Mock
	private SyncedMessageRepository syncedMsgRepo;
	
	@Mock
	private Environment mockEnv;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(ReceiverContext.class);
		PowerMockito.mockStatic(CamelUtils.class);
		setInternalState(SiteMessageConsumer.class, "initialized", true);
		siteInfo = new SiteInfo();
		siteInfo.setIdentifier("testSite");
		Mockito.when(mockProducerTemplate.getCamelContext()).thenReturn(mockCamelContext);
		Mockito.when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
		setInternalState(SiteMessageConsumer.class, "GET_JPA_URI", (Object) null);
	}
	
	private void setupConsumer(final int size) {
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(size);
		consumer = new SiteMessageConsumer(URI_MSG_PROCESSOR, siteInfo, executor);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, mockProducerTemplate);
		Whitebox.setInternalState(consumer, SyncedMessageRepository.class, syncedMsgRepo);
		setInternalState(SiteMessageConsumer.class, "GET_JPA_URI", JPA_URI_PREFIX + DEFAULT_TASK_BATCH_SIZE);
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
	public void initIfNecessary_shouldInitializeStaticFields() {
		setupConsumer(1);
		setInternalState(SiteMessageConsumer.class, "initialized", false);
		setInternalState(SiteMessageConsumer.class, "taskThreshold", 0);
		setInternalState(SiteMessageConsumer.class, "GET_JPA_URI", (Object) null);
		final int batchSize = 4;
		Mockito.when(mockEnv.getProperty(PROP_SYNC_TASK_BATCH_SIZE, Integer.class, DEFAULT_TASK_BATCH_SIZE))
		        .thenReturn(batchSize);
		
		consumer.initIfNecessary();
		
		Assert.assertTrue(getInternalState(SiteMessageConsumer.class, "initialized"));
		final int expected = executor.getMaximumPoolSize() * THREAD_THRESHOLD_MULTIPLIER;
		assertEquals(expected, ((Integer) getInternalState(SiteMessageConsumer.class, "taskThreshold")).intValue());
		assertEquals(JPA_URI_PREFIX + batchSize, getInternalState(SiteMessageConsumer.class, "GET_JPA_URI"));
		
	}
	
	@Test
	public void initIfNecessary_shouldSkipIfAlreadyInitialized() {
		setupConsumer(1);
		setInternalState(SiteMessageConsumer.class, "taskThreshold", 0);
		final String jpaUrl = JPA_URI_PREFIX + DEFAULT_TASK_BATCH_SIZE;
		assertEquals(true, getInternalState(SiteMessageConsumer.class, "initialized"));
		assertEquals(0, ((Integer) getInternalState(SiteMessageConsumer.class, "taskThreshold")).intValue());
		assertEquals(jpaUrl, getInternalState(SiteMessageConsumer.class, "GET_JPA_URI"));
		
		consumer.initIfNecessary();
		
		assertEquals(true, getInternalState(SiteMessageConsumer.class, "initialized"));
		assertEquals(0, ((Integer) getInternalState(SiteMessageConsumer.class, "taskThreshold")).intValue());
		assertEquals(jpaUrl, getInternalState(SiteMessageConsumer.class, "GET_JPA_URI"));
	}
	
	@Test
	public void processMessages_shouldProcessAllSnapshotMessagesInParallelForSlowThreads() throws Exception {
		Thread originalThread = Thread.currentThread();
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
		setupConsumer(size);
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
	
	@Test
	public void processMessage_shouldAddTheMessageToTheSyncedQueueAndDeleteTheProcessedMessage() throws Exception {
		setupConsumer(1);
		Whitebox.setInternalState(consumer, "messageProcessorUri", MOCK_PROCESSOR_URI);
		final int msgId = 1;
		SyncMessage msg = createMessage(msgId, false);
		msg.setMessageUuid("msg-uuid");
		msg.setEntityPayload("{}");
		msg.setDateSentBySender(LocalDateTime.now());
		msg.setDateCreated(new Date());
		long timestamp = System.currentTimeMillis();
		
		List<SyncMessage> processedMsgs = new ArrayList(1);
		Mockito.when(CamelUtils.send(eq(MOCK_PROCESSOR_URI), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			processedMsgs.add(exchange.getIn().getBody(SyncMessage.class));
			exchange.setProperty(EX_PROP_MSG_PROCESSED, true);
			return null;
		});
		
		List<SyncedMessage> archivedMsgs = new ArrayList(1);
		Mockito.when(syncedMsgRepo.save(any(SyncedMessage.class))).thenAnswer(invocation -> {
			archivedMsgs.add(invocation.getArgument(0));
			return null;
		});
		
		consumer.processMessage(msg);
		
		assertEquals(1, processedMsgs.size());
		assertEquals(msg, processedMsgs.get(0));
		verify(syncedMsgRepo).save(any(SyncedMessage.class));
		verify(mockProducerTemplate).sendBody("jpa:" + ENTITY + "?query=DELETE FROM " + ENTITY + " WHERE id = " + msgId,
		    null);
		assertEquals(1, archivedMsgs.size());
		SyncedMessage archive = archivedMsgs.get(0);
		assertEquals(msg.getMessageUuid(), archive.getMessageUuid());
		assertEquals(msg.getModelClassName(), archive.getModelClassName());
		assertEquals(msg.getIdentifier(), archive.getIdentifier());
		assertEquals(msg.getEntityPayload(), archive.getEntityPayload());
		assertEquals(msg.getSite(), archive.getSite());
		assertEquals(msg.getSnapshot(), archive.getSnapshot());
		assertEquals(msg.getDateSentBySender(), archive.getDateSentBySender());
		assertEquals(msg.getDateCreated(), archive.getDateReceived());
		assertEquals(SyncOutcome.SUCCESS, archive.getOutcome());
		assertTrue(archive.getDateCreated().getTime() == timestamp || archive.getDateCreated().getTime() > timestamp);
	}
	
	@Test
	public void processMessage_shouldAddTheConflictMessageToTheSyncedQueue() {
		setupConsumer(1);
		Whitebox.setInternalState(consumer, "messageProcessorUri", MOCK_PROCESSOR_URI);
		final Long msgId = 2L;
		SyncMessage msg = new SyncMessage();
		msg.setId(msgId);
		List<SyncMessage> processedMsgs = new ArrayList(1);
		Mockito.when(CamelUtils.send(eq(MOCK_PROCESSOR_URI), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			processedMsgs.add(exchange.getIn().getBody(SyncMessage.class));
			exchange.setProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE, true);
			return null;
		});
		
		List<SyncedMessage> archivedMsgs = new ArrayList(1);
		Mockito.when(syncedMsgRepo.save(any(SyncedMessage.class))).thenAnswer(invocation -> {
			archivedMsgs.add(invocation.getArgument(0));
			return null;
		});
		
		consumer.processMessage(msg);
		
		assertEquals(1, processedMsgs.size());
		assertEquals(msg, processedMsgs.get(0));
		verify(syncedMsgRepo).save(any(SyncedMessage.class));
		verify(mockProducerTemplate).sendBody("jpa:" + ENTITY + "?query=DELETE FROM " + ENTITY + " WHERE id = " + msgId,
		    null);
		assertEquals(1, archivedMsgs.size());
		assertEquals(SyncOutcome.CONFLICT, archivedMsgs.get(0).getOutcome());
	}
	
	@Test
	public void processMessage_shouldAddTheErrorMessageToTheSyncedQueue() {
		setupConsumer(1);
		Whitebox.setInternalState(consumer, "messageProcessorUri", MOCK_PROCESSOR_URI);
		final Long msgId = 2L;
		SyncMessage msg = new SyncMessage();
		msg.setId(msgId);
		List<SyncMessage> processedMsgs = new ArrayList(1);
		Mockito.when(CamelUtils.send(eq(MOCK_PROCESSOR_URI), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			processedMsgs.add(exchange.getIn().getBody(SyncMessage.class));
			exchange.setProperty(EX_PROP_MOVED_TO_ERROR_QUEUE, true);
			return null;
		});
		
		List<SyncedMessage> archivedMsgs = new ArrayList(1);
		Mockito.when(syncedMsgRepo.save(any(SyncedMessage.class))).thenAnswer(invocation -> {
			archivedMsgs.add(invocation.getArgument(0));
			return null;
		});
		
		consumer.processMessage(msg);
		
		assertEquals(1, processedMsgs.size());
		assertEquals(msg, processedMsgs.get(0));
		verify(syncedMsgRepo).save(any(SyncedMessage.class));
		verify(mockProducerTemplate).sendBody("jpa:" + ENTITY + "?query=DELETE FROM " + ENTITY + " WHERE id = " + msgId,
		    null);
		assertEquals(1, archivedMsgs.size());
		assertEquals(SyncOutcome.ERROR, archivedMsgs.get(0).getOutcome());
	}
	
	@Test
	public void processMessage_shouldFailIfTheSyncOutComeIsUnknown() {
		setupConsumer(1);
		Whitebox.setInternalState(consumer, "messageProcessorUri", MOCK_PROCESSOR_URI);
		final Long msgId = 2L;
		SyncMessage msg = new SyncMessage();
		msg.setId(msgId);
		List<SyncMessage> processedMsgs = new ArrayList(1);
		Mockito.when(CamelUtils.send(eq(MOCK_PROCESSOR_URI), any(Exchange.class))).thenAnswer(invocation -> {
			Exchange exchange = invocation.getArgument(1);
			processedMsgs.add(exchange.getIn().getBody(SyncMessage.class));
			return null;
		});
		
		Exception thrown = Assert.assertThrows(EIPException.class, () -> consumer.processMessage(msg));
		assertEquals("Something went wrong while processing sync message with id: " + msgId, thrown.getMessage());
		assertEquals(1, processedMsgs.size());
		assertEquals(msg, processedMsgs.get(0));
		verifyNoInteractions(syncedMsgRepo);
		verify(mockProducerTemplate, never()).sendBody(anyString(), isNull());
	}
	
}
