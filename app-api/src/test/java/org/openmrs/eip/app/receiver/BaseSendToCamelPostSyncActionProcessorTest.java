package org.openmrs.eip.app.receiver;

import static org.mockito.Mockito.verify;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_CLEAR_CACHE;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.apache.camel.ProducerTemplate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.powermock.reflect.Whitebox;

public class BaseSendToCamelPostSyncActionProcessorTest {
	
	private BaseSendToCamelPostSyncActionProcessor processor;
	
	@Mock
	private ProducerTemplate mockTemplate;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new CacheEvictingProcessor(mockTemplate, null, null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void processItem_shouldSendTheItemToTheEndpointUri() {
		processor = Mockito.spy(processor);
		SyncedMessage msg = new SyncedMessage();
		Mockito.doNothing().when(processor).send(URI_CLEAR_CACHE, msg, mockTemplate);
		Mockito.doNothing().when(processor).onSuccess(msg);
		
		processor.processItem(msg);
		
		verify(processor).send(URI_CLEAR_CACHE, msg, mockTemplate);
		verify(processor).onSuccess(msg);
	}
	
}
