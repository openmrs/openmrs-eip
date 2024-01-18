package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;

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
		protected int getItemSize() {
			return ITEM_SIZE;
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
	
	private static final int ITEM_SIZE = 32;
	
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
	private BytesMessage mockMsg;
	
	@Test
	public void send_shouldSendTheItemsInTheBatchBuffer() throws Exception {
		when(mockConnFactory.createConnection()).thenReturn(mockConnection);
		when(mockConnection.createSession()).thenReturn(mockSession);
		when(mockSession.createQueue(QUEUE_NAME)).thenReturn(mockQueue);
		when(mockSession.createProducer(mockQueue)).thenReturn(mockMsgProducer);
		when(mockSession.createBytesMessage()).thenReturn(mockMsg);
		final Long id = 3L;
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setId(id);
		byte[] expectedSentBytes = JsonUtils.marshallToStream(List.of(msg), ITEM_SIZE).toByteArray();
		MockSyncBatchManager manager = new MockSyncBatchManager();
		manager.add(msg);
		
		manager.send();
		
		Mockito.verify(mockMsg).setIntProperty(SyncConstants.SYNC_BATCH_PROP_SIZE, 1);
		ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);
		Mockito.verify(mockMsg).writeBytes(bytesCaptor.capture());
		assertTrue(Arrays.equals(expectedSentBytes, bytesCaptor.getValue()));
		Mockito.verify(mockMsgProducer).send(mockMsg);
		Mockito.verify(mockMsgProducer).send(mockMsg);
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
