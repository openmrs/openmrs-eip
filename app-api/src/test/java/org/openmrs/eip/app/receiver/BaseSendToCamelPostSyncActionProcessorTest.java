package org.openmrs.eip.app.receiver;

import static org.mockito.Mockito.verify;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_CLEAR_CACHE;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.repository.PostSyncActionRepository;
import org.openmrs.eip.component.exception.EIPException;

public class BaseSendToCamelPostSyncActionProcessorTest {
	
	private BaseSendToCamelPostSyncActionProcessor processor;
	
	@Mock
	private ProducerTemplate mockTemplate;
	
	@Mock
	private PostSyncActionRepository mockRepo;
	
	@Mock
	private PostSyncAction mockAction;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		processor = new CacheEvictingProcessor(mockTemplate, mockRepo);
	}
	
	@Test
	public void processItem_shouldSendTheItemToTheEndpointUri() {
		processor = Mockito.spy(processor);
		Mockito.doNothing().when(processor).send(URI_CLEAR_CACHE, mockAction, mockTemplate);
		
		processor.processItem(mockAction);
		
		verify(processor).send(URI_CLEAR_CACHE, mockAction, mockTemplate);
	}
	
	@Test
	public void onSuccess_shouldMarkTheItemAsCompletedAndSaveTheChanges() {
		processor.onSuccess(mockAction);
		
		verify(mockAction).markAsCompleted();
		verify(mockRepo).save(mockAction);
	}
	
	@Test
	public void onFailure_shouldMarkTheItemAsFailedAndSaveTheChanges() {
		final String msg = "test";
		Throwable t = new EIPException(msg);
		
		processor.onFailure(mockAction, t);
		
		verify(mockAction).markAsProcessedWithError(t.toString());
		verify(mockRepo).save(mockAction);
	}
	
}
