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
public class BaseSearchIndexUpdatingProcessorTest {
	
	@Mock
	private CustomHttpClient mockClient;
	
	@Mock
	private String mockPayload;
	
	private BaseSearchIndexUpdatingProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverUtils.class);
		processor = new RetrySearchIndexUpdatingProcessor(mockClient);
	}
	
	@Test
	public void convertBody_shouldGenerateTheOpenmrsSearchIndexUpdatePayload() {
		final String uuid = "some-uuid";
		final SyncOperation op = SyncOperation.c;
		final String modelClass = PersonModel.class.getName();
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		retry.setModelClassName(modelClass);
		retry.setOperation(op);
		
		processor.convertBody(retry);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.generateSearchIndexUpdatePayload(modelClass, uuid, op);
	}
	
	@Test
	public void process_shouldProcessAnIndexedEntity() {
		final String uuid = "some-uuid";
		final SyncOperation op = SyncOperation.c;
		final String modelClass = PersonModel.class.getName();
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		retry.setModelClassName(modelClass);
		retry.setOperation(op);
		when(ReceiverUtils.isIndexed(modelClass)).thenReturn(true);
		when(ReceiverUtils.generateSearchIndexUpdatePayload(modelClass, uuid, op)).thenReturn(mockPayload);
		
		processor.process(retry);
		
		Mockito.verify(mockClient).sendRequest(HttpRequestProcessor.INDEX_RESOURCE, mockPayload);
	}
	
	@Test
	public void process_shouldNotProcessANonIndexedEntity() {
		final String modelClass = VisitModel.class.getName();
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setModelClassName(modelClass);
		
		processor.process(retry);
		
		PowerMockito.verifyStatic(ReceiverUtils.class, never());
		ReceiverUtils.generateSearchIndexUpdatePayload(any(), any(), any());
		Mockito.verifyNoInteractions(mockClient);
	}
	
}
