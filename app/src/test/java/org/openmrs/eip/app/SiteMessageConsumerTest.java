package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;
import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_SYNC_THREAD_SIZE;
import static org.openmrs.eip.app.SyncConstants.ROUTE_URI_SYNC_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReceiverContext.class })
public class SiteMessageConsumerTest {
	
	private SiteMessageConsumer consumer;
	
	private ExecutorService executor;
	
	private SiteInfo siteInfo;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverContext.class);
		siteInfo = new SiteInfo();
		siteInfo.setIdentifier("testSite");
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
	public void processMessages_shouldProcessAllSnapshotMessagesInParallel() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		executor = Executors.newFixedThreadPool(size);
		consumer = new SiteMessageConsumer(siteInfo, executor, size);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, mockProducerTemplate);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, true);
			messages.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				SyncMessage arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(ROUTE_URI_SYNC_PROCESSOR, m);
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			Assert.assertTrue(expectedResults.contains(msg.getId()));
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
	}
	
	@Test
	public void processMessages_shouldProcessAllNonSnapshotMessagesInSerialInCurrentThread() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 10;
		executor = Executors.newFixedThreadPool(size);
		consumer = new SiteMessageConsumer(siteInfo, executor, size);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, mockProducerTemplate);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = new ArrayList(size);
		List<String> threadNames = new ArrayList(size);
		
		for (int i = 0; i < size; i++) {
			SyncMessage m = createMessage(i, false);
			messages.add(m);
			Mockito.doAnswer(invocation -> {
				SyncMessage arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				threadNames.add(Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(ROUTE_URI_SYNC_PROCESSOR, m);
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, threadNames.size());
		
		for (int i = 0; i < size; i++) {
			Assert.assertEquals(Long.valueOf(i), expectedResults.get(i));
			SyncMessage msg = messages.get(i);
			String threadName = threadNames.get(i);
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(consumer.getThreadName(msg), threadName.split(":")[1]);
		}
	}
	
	@Test
	public void processMessages_shouldProcessAMixOfSnapshotAndNonSnapshotMessagesAndMaintainTheIndicesOfNonSnapshots()
	    throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 100;
		executor = Executors.newFixedThreadPool(size);
		consumer = new SiteMessageConsumer(siteInfo, executor, size);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, mockProducerTemplate);
		List<SyncMessage> messages = new ArrayList(size);
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
			SyncMessage m = createMessage(i, nonSnapshotMsgIndices.contains(i) ? false : true);
			messages.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				SyncMessage arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(ROUTE_URI_SYNC_PROCESSOR, m);
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			Assert.assertTrue(expectedResults.contains(msg.getId()));
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
		
		//Non-snapshot message are only processed after all snapshot messages ahead of the so the order which they are
		//processed is preserved and should have been processed in the current thread
		for (Integer i : nonSnapshotMsgIndices) {
			SyncMessage msg = messages.get(i);
			String threadName = expectedMsgIdThreadNameMap.get(msg.getId());
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
	}
	
	@Test
	public void processMessages_shouldProcessAMixOfMessagesWithAllSnapshotAtTheStartOfTheQueue() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 50;
		executor = Executors.newFixedThreadPool(size);
		consumer = new SiteMessageConsumer(siteInfo, executor, size);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, mockProducerTemplate);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		for (int i = 0; i < size / 2; i++) {
			SyncMessage m = createMessage(i, true);
			messages.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				SyncMessage arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(ROUTE_URI_SYNC_PROCESSOR, m);
		}
		
		List<Integer> nonSnapshotMsgIndices = new ArrayList();
		for (int i = (size / 2); i < size; i++) {
			nonSnapshotMsgIndices.add(i);
			SyncMessage m = createMessage(i, false);
			messages.add(m);
			Mockito.doAnswer(invocation -> {
				SyncMessage arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(ROUTE_URI_SYNC_PROCESSOR, m);
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		assertEquals(size / 2, nonSnapshotMsgIndices.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			Assert.assertTrue(expectedResults.contains(msg.getId()));
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
		
		//Non-snapshot message are only processed after all snapshot messages ahead of the so the order which they are
		//processed is preserved and should have been processed in the current thread
		for (Integer i : nonSnapshotMsgIndices) {
			SyncMessage msg = messages.get(i);
			String threadName = expectedMsgIdThreadNameMap.get(msg.getId());
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
		
		//Snapshots should all have been first synced before incremental events
		for (int i = 0; i < size / 2; i++) {
			Assert.assertTrue(messages.get(expectedResults.get(i).intValue()).getSnapshot());
		}
		
		for (int i = (size / 2); i < size; i++) {
			Assert.assertFalse(messages.get(expectedResults.get(i).intValue()).getSnapshot());
		}
	}
	
	@Test
	public void processMessages_shouldProcessAMixOfMessagesWithAllSnapshotAtTheEndOfTheQueue() throws Exception {
		final String originalThreadName = Thread.currentThread().getName();
		final int size = 50;
		executor = Executors.newFixedThreadPool(size);
		consumer = new SiteMessageConsumer(siteInfo, executor, size);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, mockProducerTemplate);
		List<SyncMessage> messages = new ArrayList(size);
		List<Long> expectedResults = synchronizedList(new ArrayList(size));
		Map<Long, String> expectedMsgIdThreadNameMap = new ConcurrentHashMap(size);
		
		List<Integer> nonSnapshotMsgIndices = new ArrayList();
		for (int i = 0; i < size / 2; i++) {
			nonSnapshotMsgIndices.add(i);
			SyncMessage m = createMessage(i, false);
			messages.add(m);
			Mockito.doAnswer(invocation -> {
				SyncMessage arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(ROUTE_URI_SYNC_PROCESSOR, m);
		}
		
		for (int i = (size / 2); i < size; i++) {
			SyncMessage m = createMessage(i, true);
			messages.add(m);
			Mockito.doAnswer(invocation -> {
				Thread.sleep(500);
				SyncMessage arg = invocation.getArgument(1);
				expectedResults.add(arg.getId());
				expectedMsgIdThreadNameMap.put(arg.getId(), Thread.currentThread().getName());
				return null;
			}).when(mockProducerTemplate).sendBody(ROUTE_URI_SYNC_PROCESSOR, m);
		}
		
		consumer.processMessages(messages);
		
		assertEquals(originalThreadName, Thread.currentThread().getName());
		assertEquals(size, expectedResults.size());
		assertEquals(size, expectedMsgIdThreadNameMap.size());
		assertEquals(size / 2, nonSnapshotMsgIndices.size());
		
		for (int i = 0; i < size; i++) {
			SyncMessage msg = messages.get(i);
			Assert.assertTrue(expectedResults.contains(msg.getId()));
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
		
		//Non-snapshot message are only processed after all snapshot messages ahead of the so the order which they are
		//processed is preserved and should have been processed in the current thread
		for (Integer i : nonSnapshotMsgIndices) {
			SyncMessage msg = messages.get(i);
			String threadName = expectedMsgIdThreadNameMap.get(msg.getId());
			assertEquals(originalThreadName, threadName.split(":")[0]);
			assertEquals(consumer.getThreadName(msg), expectedMsgIdThreadNameMap.get(msg.getId()).split(":")[1]);
		}
		
		//Incremental events should all have been first synced before snapshot
		for (int i = 0; i < size / 2; i++) {
			Assert.assertFalse(messages.get(expectedResults.get(i).intValue()).getSnapshot());
		}
		
		for (int i = (size / 2); i < size; i++) {
			Assert.assertTrue(messages.get(expectedResults.get(i).intValue()).getSnapshot());
		}
	}
	
}
