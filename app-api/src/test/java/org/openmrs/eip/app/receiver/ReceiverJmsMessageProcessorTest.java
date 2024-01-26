package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.nio.charset.StandardCharsets;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
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
	private CamelContext mockContext;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(CamelUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ReceiverJmsMessageProcessor(null, null, mockContext);
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
	public void getUniqueId_shouldReturnIdForAReconciliationMessage() {
		final Long id = 2L;
		JmsMessage msg = new JmsMessage();
		msg.setId(id);
		msg.setType(MessageType.RECONCILIATION);
		assertEquals(id.toString(), processor.getUniqueId(msg));
	}
	
	@Test
	public void getLogicalType_shouldReturnType() {
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.SYNC);
		assertEquals(MessageType.SYNC.name(), processor.getLogicalType(msg));
	}
	
	@Test
	public void processItem_shouldProcessASyncMessage() throws Exception {
		final String body = "{}";
		JmsMessage msg = new JmsMessage();
		msg.setType(MessageType.SYNC);
		msg.setBody(body.getBytes(StandardCharsets.UTF_8));
		
		processor.processItem(msg);
		
		ArgumentCaptor<Exchange> exArgCaptor = ArgumentCaptor.forClass(Exchange.class);
		PowerMockito.verifyStatic(CamelUtils.class);
		CamelUtils.send(ArgumentMatchers.eq(ReceiverConstants.URI_RECEIVER_MAIN), exArgCaptor.capture());
		Exchange exchange = exArgCaptor.getValue();
		Assert.assertEquals(body, exchange.getIn().getBody());
		Assert.assertEquals(mockContext, exchange.getContext());
	}
	
}
