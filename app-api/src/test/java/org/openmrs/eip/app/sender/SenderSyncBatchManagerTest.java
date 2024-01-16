package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.reflect.Whitebox;

public class SenderSyncBatchManagerTest {
	
	private SenderSyncBatchManager manager;
	
	@Before
	public void setup() {
		manager = new SenderSyncBatchManager(null);
	}
	
	@Test
	public void getQueueName_shouldReturnTheNameOfTheJmsQueue() {
		final String queueName = "activemq:openmrs.sync";
		final String endpoint = "activemq:" + queueName;
		Whitebox.setInternalState(manager, "brokerEndpoint", endpoint);
		assertEquals(queueName, manager.getQueueName());
	}
	
	@Test
	public void convert_shouldConvertSyncMessageToModel() {
		final String senderId = "test";
		Whitebox.setInternalState(manager, "senderId", senderId);
		SyncMetadata metadata = new SyncMetadata();
		Assert.assertNull(metadata.getSourceIdentifier());
		Map<String, Object> syncData = Map.of("metadata", metadata);
		SenderSyncMessage msg = new SenderSyncMessage();
		msg.setData(JsonUtils.marshall(syncData));
		
		SyncModel model = manager.convert(msg);
		
		assertEquals(senderId, model.getMetadata().getSourceIdentifier());
	}
	
}
