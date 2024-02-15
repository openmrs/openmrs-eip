package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.management.service.ReceiverReconcileService;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CamelUtils.class)
public class ReceiverJmsMessageProcessorTest {
	
	private ReceiverJmsMessageProcessor processor;
	
	@Mock
	private ReceiverService mockReceiverService;
	
	@Mock
	private ReceiverReconcileService mockReconcileService;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(CamelUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ReceiverJmsMessageProcessor(null, mockReceiverService, mockReconcileService);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getUniqueId_shouldReturnTypeForASyncMessage() {
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.SYNC);
		assertEquals(MessageType.SYNC.name(), processor.getUniqueId(msg));
	}
	
	@Test
	public void getUniqueId_shouldReturnSiteIdentifierForAReconciliationMessage() {
		final String siteIdentifier = "test";
		JmsMessage msg = new JmsMessage();
		msg.setSiteId(siteIdentifier);
		msg.setType(MessageType.RECONCILE);
		assertEquals(siteIdentifier, processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnType() {
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.SYNC);
		assertEquals(MessageType.SYNC.name(), processor.getLogicalType(msg));
	}
	
	@Test
	public void processItem_shouldProcessASyncMessage() {
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.SYNC);
		
		processor.processItem(msg);
		
		Mockito.verify(mockReceiverService).processJmsMessage(msg);
	}
	
	@Test
	public void processItem_shouldProcessAReconcileMessage() {
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.RECONCILE);
		
		processor.processItem(msg);
		
		Mockito.verify(mockReconcileService).processJmsMessage(msg);
	}
	
}
