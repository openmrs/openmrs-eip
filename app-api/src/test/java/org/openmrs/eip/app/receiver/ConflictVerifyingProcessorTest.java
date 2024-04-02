package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.HashUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HashUtils.class)
public class ConflictVerifyingProcessorTest {
	
	private ConflictVerifyingProcessor processor;
	
	@Mock
	private ConflictService mockService;
	
	@Mock
	private EntityServiceFacade mockEntityService;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(HashUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ConflictVerifyingProcessor(null, mockService, mockEntityService);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void getThreadName_shouldReturnArchiveMessageUuid() {
		final String messageUuid = "message-uuid";
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setMessageUuid(messageUuid);
		assertEquals(messageUuid, processor.getThreadName(conflict));
	}
	
	@Test
	public void processItem_shouldNotMoveTheConflictIfTheHashOnFileDoesNotMatchThatOfTheDbState() {
		ConflictQueueItem c = new ConflictQueueItem();
		c.setModelClassName(PersonModel.class.getName());
		final Long id = 3L;
		final String uuid = "test-uuid";
		c.setId(id);
		c.setIdentifier(uuid);
		BaseModel model = new PersonModel();
		when(mockEntityService.getModel(TableToSyncEnum.PERSON, uuid)).thenReturn(model);
		PersonHash storedHash = new PersonHash();
		storedHash.setHash("some hash");
		when(HashUtils.getStoredHash(uuid, PersonHash.class)).thenReturn(storedHash);
		when(HashUtils.computeHash(model)).thenReturn("another hash");
		
		processor.processItem(c);
		
		Mockito.verifyNoInteractions(mockService);
	}
	
	@Test
	public void processItem_shouldNotMoveTheConflictIfNoHashIsFoundOnFile() {
		ConflictQueueItem c = new ConflictQueueItem();
		c.setModelClassName(PersonModel.class.getName());
		final String uuid = "test-uuid";
		c.setIdentifier(uuid);
		when(mockEntityService.getModel(TableToSyncEnum.PERSON, uuid)).thenReturn(new PersonModel());
		
		processor.processItem(c);
		
		Mockito.verifyNoInteractions(mockService);
	}
	
	@Test
	public void processItem_shouldMoveTheConflictToTheRetryQueueIfTheHashOnFileIsInvalid() {
		ConflictQueueItem c = new ConflictQueueItem();
		c.setModelClassName(PersonModel.class.getName());
		final String uuid = "test-uuid";
		c.setIdentifier(uuid);
		BaseModel model = new PersonModel();
		when(mockEntityService.getModel(TableToSyncEnum.PERSON, uuid)).thenReturn(model);
		final String hash = "valid-hash";
		PersonHash storedHash = new PersonHash();
		storedHash.setHash(hash);
		when(HashUtils.getStoredHash(uuid, PersonHash.class)).thenReturn(storedHash);
		when(HashUtils.computeHash(model)).thenReturn(hash);
		
		processor.processItem(c);
		
		final String reason = "Moved from conflict queue because the hash on file is valid";
		Mockito.verify(mockService).moveToRetryQueue(c, reason);
	}
	
	@Test
	public void processItem_shouldMoveTheConflictToTheRetryQueueIfNoEntityIsFoundInTheDatabase() {
		ConflictQueueItem c = new ConflictQueueItem();
		c.setModelClassName(PersonModel.class.getName());
		final Long id = 3L;
		c.setId(id);
		
		processor.processItem(c);
		
		final String reason = "No entity found in the database associated to conflict item with id: " + id;
		Mockito.verify(mockService).moveToRetryQueue(c, reason);
	}
	
}
