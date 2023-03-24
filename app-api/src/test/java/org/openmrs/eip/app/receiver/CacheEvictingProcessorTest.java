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
public class CacheEvictingProcessorTest {
	
	private CacheEvictingProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new CacheEvictingProcessor(null, null, null);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getProcessorName_shouldReturnTheProcessorName() {
		assertEquals("cache evict", processor.getProcessorName());
	}
	
	@Test
	public void getQueueName_shouldReturnTheQueueName() {
		assertEquals("cache-evict", processor.getQueueName());
	}
	
	@Test
	public void onSuccess_shouldMarkTheMessageAsProcessed() {
		SyncedMessage msg = new SyncedMessage();
		assertFalse(msg.isEvictedFromCache());
		SyncedMessageRepository mockRepo = Mockito.mock(SyncedMessageRepository.class);
		processor = new CacheEvictingProcessor(null, null, mockRepo);
		
		processor.onSuccess(msg);
		
		assertTrue(msg.isEvictedFromCache());
		Mockito.verify(mockRepo).save(msg);
	}
	
	@Test
	public void convertBody_shouldGenerateTheOpenmrsCacheEvictionPayload() {
		final String uuid = "some-uuid";
		final SyncOperation op = SyncOperation.c;
		final String modelClass = PersonModel.class.getName();
		SyncedMessage msg = new SyncedMessage();
		msg.setIdentifier(uuid);
		msg.setModelClassName(modelClass);
		msg.setOperation(op);
		
		processor.convertBody(msg);
		
		PowerMockito.verifyStatic(ReceiverUtils.class);
		ReceiverUtils.generateEvictionPayload(modelClass, uuid, op);
	}
	
	@Test
	public void isSquashed_shouldReturnTrueForAMessageMarkedAsCached() {
		SyncedMessage msg = new SyncedMessage();
		msg.setEvictedFromCache(true);
		assertTrue(processor.isSquashed(msg));
	}
	
	@Test
	public void isSquashed_shouldReturnFalseForAMessageMarkedAsCached() {
		assertFalse(processor.isSquashed(new SyncedMessage()));
	}
	
	@Test
	public void updateSquashedMessage_shouldMarkedTheMessageAsCached() {
		SyncedMessage msg = new SyncedMessage();
		assertFalse(msg.isEvictedFromCache());
		
		processor.updateSquashedMessage(msg);
		
		assertTrue(msg.isEvictedFromCache());
	}
	
}
