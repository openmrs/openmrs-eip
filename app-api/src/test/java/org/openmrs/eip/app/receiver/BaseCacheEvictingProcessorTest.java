package org.openmrs.eip.app.receiver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReceiverUtils.class)
public class BaseCacheEvictingProcessorTest {
	
	@Mock
	private CustomHttpClient mockClient;
	
	@Mock
	private String mockPayload;
	
	private BaseCacheEvictingProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverUtils.class);
		processor = new RetryCacheEvictingProcessor(mockClient);
	}
	
	@Test
	public void convertBody_shouldGenerateTheOpenmrsCacheEvictionPayload() {
		final String uuid = "some-uuid";
		final SyncOperation op = SyncOperation.c;
		final String modelClass = PersonModel.class.getName();
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		retry.setModelClassName(modelClass);
		retry.setOperation(op);
		
		processor.convertBody(retry);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.generateEvictionPayload(modelClass, uuid, op);
	}
	
	@Test
	public void process_shouldProcessACachedEntity() {
		final String uuid = "some-uuid";
		final SyncOperation op = SyncOperation.c;
		final String modelClass = PersonModel.class.getName();
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		retry.setModelClassName(modelClass);
		retry.setOperation(op);
		when(ReceiverUtils.isCached(modelClass)).thenReturn(true);
		when(ReceiverUtils.generateEvictionPayload(modelClass, uuid, op)).thenReturn(mockPayload);
		
		processor.process(retry);
		
		Mockito.verify(mockClient).sendRequest(HttpRequestProcessor.CACHE_RESOURCE, mockPayload);
	}
	
	@Test
	public void process_shouldNotProcessANonCachedEntity() {
		final String modelClass = VisitModel.class.getName();
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setModelClassName(modelClass);
		
		processor.process(retry);
		
		PowerMockito.verifyStatic(ReceiverUtils.class, never());
		ReceiverUtils.generateEvictionPayload(any(), any(), any());
		Mockito.verifyNoInteractions(mockClient);
	}
	
}
