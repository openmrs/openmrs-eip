package org.openmrs.eip.app;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;

public class SendToCamelEndpointProcessorTest {
	
	class MockProcessor implements SendToCamelEndpointProcessor {}
	
	@Mock
	private ProducerTemplate mockTemplate;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	private SendToCamelEndpointProcessor processor = new MockProcessor();
	
	@Test
	public void send_shouldSendTheItemToTheEndpoint() {
		String uri = "test:uri";
		SyncedMessage msg = new SyncedMessage();
		processor = Mockito.spy(processor);
		
		processor.send(uri, msg, mockTemplate);
		
		Mockito.verify(processor).convertBody(msg);
		Mockito.verify(mockTemplate).sendBody(uri, msg);
	}
	
	@Test
	public void send_shouldSendAllItemsToTheEndpointForACollection() {
		String uri = "test:uri";
		SyncedMessage msg1 = new SyncedMessage();
		SyncedMessage msg2 = new SyncedMessage();
		List<SyncedMessage> messages = Arrays.asList(msg1, msg2);
		processor = Mockito.spy(processor);
		
		processor.send(uri, messages, mockTemplate);
		
		Mockito.verify(processor).convertBody(messages);
		Mockito.verify(mockTemplate).sendBody(uri, msg1);
		Mockito.verify(mockTemplate).sendBody(uri, msg2);
	}
	
	@Test
	public void convertBody_shouldReturnTheSameItem() {
		SyncedMessage msg = new SyncedMessage();
		
		Assert.assertEquals(msg, processor.convertBody(msg));
	}
	
}
