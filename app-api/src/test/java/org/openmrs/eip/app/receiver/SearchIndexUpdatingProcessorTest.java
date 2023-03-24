package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.PersonModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReceiverUtils.class)
public class SearchIndexUpdatingProcessorTest {
	
	private SearchIndexUpdatingProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SearchIndexUpdatingProcessor(null, null, null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("search index update", processor.getProcessorName());
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("search-index-update", processor.getQueueName());
	}
	
	@Test
	public void onSuccess_shouldMarkTheMessageAsProcessed() {
		SyncedMessage msg = new SyncedMessage();
		assertFalse(msg.isSearchIndexUpdated());
		SyncedMessageRepository mockRepo = Mockito.mock(SyncedMessageRepository.class);
		processor = new SearchIndexUpdatingProcessor(null, null, mockRepo);
		
		processor.onSuccess(msg);
		
		assertTrue(msg.isSearchIndexUpdated());
		Mockito.verify(mockRepo).save(msg);
	}
	
	@Test
	public void convertBody_shouldGenerateTheOpenmrsSearchIndexUpdatePayload() {
		final String uuid = "some-uuid";
		final SyncOperation op = SyncOperation.c;
		final String modelClass = PersonModel.class.getName();
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(modelClass);
		msg.setOperation(op);
		
		processor.convertBody(msg);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.generateSearchIndexUpdatePayload(modelClass, uuid, op);
	}
	
	@Test
	public void isSquashed_shouldReturnTrueForAMessageMarkedAsIndexed() {
		SyncedMessage msg = new SyncedMessage();
		msg.setSearchIndexUpdated(true);
		assertTrue(processor.isSquashed(msg));
	}
	
	@Test
	public void isSquashed_shouldReturnFalseForAMessageMarkedAsIndexed() {
		assertFalse(processor.isSquashed(new SyncedMessage()));
	}
	
	@Test
	public void updateSquashedMessage_shouldMarkedTheMessageAsIndexed() {
		SyncedMessage msg = new SyncedMessage();
		assertFalse(msg.isSearchIndexUpdated());
		
		processor.updateSquashedMessage(msg);
		
		assertTrue(msg.isSearchIndexUpdated());
	}
	
}
