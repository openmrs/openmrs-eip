package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;

@RunWith(PowerMockRunner.class)
public class BaseSyncBatchManagerTest {
	
	public class MockSyncBatchManager extends BaseSyncBatchManager {
		
		protected List<Long> updatedItemIds;
		
		protected List<Object> convertedItems = new ArrayList<>();
		
		public MockSyncBatchManager() {
			super(mockConnFactory);
		}
		
		@Override
		protected String getQueueName() {
			return QUEUE_NAME;
		}
		
		@Override
		protected int getBatchSize() {
			return 10;
		}
		
		@Override
		protected Object convert(AbstractEntity item) {
			convertedItems.add(item);
			return item;
		}
		
		@Override
		protected void updateItems(List itemIds) {
			updatedItemIds = new ArrayList<>(itemIds);
		}
		
	}
	
	private static final String QUEUE_NAME = "test";
	
	@Mock
	private ConnectionFactory mockConnFactory;
	
	@Mock
	private Connection mockConnection;
	
	@Mock
	private Session mockSession;
	
	@Mock
	private Queue mockQueue;
	
	@Mock
	private MessageProducer mockMsgProducer;
	
	@Mock
	private BytesMessage mockBytesMsg;
	
	private MockSyncBatchManager manager;
	
	@Before
	public void setup() {
		manager = new MockSyncBatchManager();
		Whitebox.setInternalState(manager, "largeMsgSize", SyncConstants.DEFAULT_LARGE_MSG_SIZE);
	}
	
	@Test
	public void send_shouldSendTheItemsInTheBatchBuffer() throws Exception {
		when(mockConnFactory.createConnection()).thenReturn(mockConnection);
		when(mockConnection.createSession()).thenReturn(mockSession);
		when(mockSession.createQueue(QUEUE_NAME)).thenReturn(mockQueue);
		when(mockSession.createProducer(mockQueue)).thenReturn(mockMsgProducer);
		when(mockSession.createBytesMessage()).thenReturn(mockBytesMsg);
		final Long id = 3L;
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setId(id);
		byte[] expectedSentBytes = JsonUtils.marshalToBytes(List.of(msg));
		manager.add(msg);
		
		manager.send();
		
		verify(mockBytesMsg).setIntProperty(SyncConstants.SYNC_BATCH_PROP_SIZE, 1);
		ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
		verify(mockBytesMsg).writeBytes(bytesCaptor.capture());
		assertTrue(Arrays.equals(expectedSentBytes, bytesCaptor.getValue()));
		verify(mockMsgProducer).send(mockBytesMsg);
		verify(mockMsgProducer).send(mockBytesMsg);
		assertEquals(manager.updatedItemIds, List.of(id));
		List<Long> itemIds = Whitebox.getInternalState(manager, "itemIds");
		assertTrue(itemIds.isEmpty());
	}
	
	@Test
	public void send_shouldCompressAndSendALargeMessage() throws Exception {
		when(mockConnFactory.createConnection()).thenReturn(mockConnection);
		when(mockConnection.createSession()).thenReturn(mockSession);
		when(mockSession.createQueue(QUEUE_NAME)).thenReturn(mockQueue);
		when(mockSession.createProducer(mockQueue)).thenReturn(mockMsgProducer);
		when(mockSession.createBytesMessage()).thenReturn(mockBytesMsg);
		final Long id = 3L;
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setId(id);
		byte[] msgBytes = JsonUtils.marshalToBytes(List.of(msg));
		Whitebox.setInternalState(manager, "largeMsgSize", msgBytes.length - 1);
		byte[] expectedSentBytes = Utils.compress(msgBytes);
		manager.add(msg);
		
		manager.send();
		
		verify(mockBytesMsg).setIntProperty(SyncConstants.SYNC_BATCH_PROP_SIZE, 1);
		ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
		verify(mockBytesMsg).writeBytes(bytesCaptor.capture());
		assertTrue(Arrays.equals(expectedSentBytes, bytesCaptor.getValue()));
		verify(mockMsgProducer).send(mockBytesMsg);
		verify(mockMsgProducer).send(mockBytesMsg);
		assertEquals(manager.updatedItemIds, List.of(id));
		List<Long> itemIds = Whitebox.getInternalState(manager, "itemIds");
		assertTrue(itemIds.isEmpty());
	}
	
	@Test
	public void send_shouldCompressAndSendALargeMessageAsAStream() throws Exception {
		StreamMessage mockStreamMsg = Mockito.mock(StreamMessage.class);
		when(mockConnFactory.createConnection()).thenReturn(mockConnection);
		when(mockConnection.createSession()).thenReturn(mockSession);
		when(mockSession.createQueue(QUEUE_NAME)).thenReturn(mockQueue);
		when(mockSession.createProducer(mockQueue)).thenReturn(mockMsgProducer);
		when(mockSession.createStreamMessage()).thenReturn(mockStreamMsg);
		final Long id = 3L;
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setId(id);
		byte[] expectedSentBytes = Utils.compress(JsonUtils.marshalToBytes(List.of(msg)));
		Whitebox.setInternalState(manager, "largeMsgSize", expectedSentBytes.length - 1);
		manager.add(msg);
		
		manager.send();
		
		verify(mockStreamMsg).setIntProperty(SyncConstants.SYNC_BATCH_PROP_SIZE, 1);
		ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
		verify(mockStreamMsg).writeBytes(bytesCaptor.capture());
		assertTrue(Arrays.equals(expectedSentBytes, bytesCaptor.getValue()));
		verify(mockMsgProducer).send(mockStreamMsg);
		verify(mockMsgProducer).send(mockStreamMsg);
		assertEquals(manager.updatedItemIds, List.of(id));
		List<Long> itemIds = Whitebox.getInternalState(manager, "itemIds");
		assertTrue(itemIds.isEmpty());
	}
	
	@Test
	public void add_shouldConvertAndStoreItemAndItsId() {
		final Long id = 3L;
		MockSyncBatchManager manager = new MockSyncBatchManager();
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setId(id);
		
		manager.add(msg);
		List<Object> items = Whitebox.getInternalState(manager, "items");
		assertTrue(items.contains(msg));
		List<Long> itemIds = Whitebox.getInternalState(manager, "itemIds");
		assertTrue(itemIds.contains(id));
		
	}
	
	@Test
	public void reset_shouldClearTheItemAndIdLists() {
		MockSyncBatchManager manager = new MockSyncBatchManager();
		SenderSyncMessage msg = new SenderSyncMessage();
		manager.add(msg);
		List<Object> items = Whitebox.getInternalState(manager, "items");
		assertFalse(items.isEmpty());
		List<Long> itemIds = Whitebox.getInternalState(manager, "itemIds");
		assertFalse(itemIds.isEmpty());
		
		manager.reset();
		
		items = Whitebox.getInternalState(manager, "items");
		assertTrue(items.isEmpty());
		itemIds = Whitebox.getInternalState(manager, "itemIds");
		assertTrue(itemIds.isEmpty());
	}
	
}
