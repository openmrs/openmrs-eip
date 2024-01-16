package org.openmrs.eip.app.sender;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class SenderSyncBatchManagerTest {
	
	private SenderSyncBatchManager manager;
	
	@Before
	public void setup() {
		manager = new SenderSyncBatchManager(10, null);
	}
	
	@Test
	public void getQueueName_shouldReturnTheNameOfTheJmsQueue() {
		final String queueName = "activemq:openmrs.sync";
		final String endpoint = "activemq:" + queueName;
		Whitebox.setInternalState(manager, "brokerEndpoint", endpoint);
		Assert.assertEquals(queueName, manager.getQueueName());
	}
	
}
