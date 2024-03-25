package org.openmrs.eip.app.receiver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.component.Constants.HASH_DELETED;
import static org.openmrs.eip.component.service.light.AbstractLightService.DEFAULT_VOID_REASON;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.ConflictsFoundException;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, HashUtils.class })
public class EntityLoaderTest {
	
	@Mock
	private EntityServiceFacade serviceFacade;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private Logger mockLogger;
	
	@Mock
	private Environment mockEnv;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private EntityLoader producer;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(HashUtils.class);
		producer = new EntityLoader(serviceFacade);
		when(SyncContext.getBean(ProducerTemplate.class)).thenReturn(mockProducerTemplate);
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
		Whitebox.setInternalState(EntityLoader.class, Logger.class, mockLogger);
	}
	
	@Test
	public void process_shouldSaveNewEntity() throws Exception {
		// Given
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("c");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonHash personHash = new PersonHash();
		assertNull(personHash.getIdentifier());
		assertNull(personHash.getHash());
		assertNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
		final String expectedHash = "testing";
		when(HashUtils.computeHash(model)).thenReturn(expectedHash);
		when(HashUtils.instantiateHashEntity(PersonHash.class)).thenReturn(personHash);
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		// When
		producer.process(syncModel);
		
		// Then
		PowerMockito.verifyStatic(HashUtils.class);
		HashUtils.saveHash(personHash, mockProducerTemplate, true);
		verify(mockLogger).debug("Inserting new hash for the incoming entity state");
		assertEquals(model.getUuid(), personHash.getIdentifier());
		assertEquals(expectedHash, personHash.getHash());
		assertNotNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
		verify(serviceFacade).saveModel(TableToSyncEnum.PERSON, model);
	}
	
	@Test
	public void process_shouldUpdateAnExistingEntity() {
		// Given
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("u");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonModel dbModel = new PersonModel();
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, model.getUuid())).thenReturn(dbModel);
		final String currentHash = "current-hash";
		PersonHash storedHash = new PersonHash();
		storedHash.setHash(currentHash);
		assertNull(storedHash.getDateChanged());
		final String expectedNewHash = "tester";
		when(HashUtils.computeHash(dbModel)).thenReturn(currentHash);
		when(HashUtils.computeHash(model)).thenReturn(expectedNewHash);
		when(HashUtils.getStoredHash(model.getUuid(), PersonHash.class, mockProducerTemplate)).thenReturn(storedHash);
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		// When
		producer.process(syncModel);
		
		// Then
		verify(serviceFacade).saveModel(TableToSyncEnum.PERSON, model);
		verify(mockLogger).debug("Updating hash for the incoming entity state");
		assertEquals(expectedNewHash, storedHash.getHash());
		assertNotNull(storedHash.getDateChanged());
	}
	
	@Test
	public void process_shouldDeleteAnEntity() {
		final String personUuid = "some-uuid";
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		BaseModel model = new PersonModel();
		model.setUuid(personUuid);
		syncModel.setModel(model);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("d");
		syncModel.setMetadata(metadata);
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, personUuid)).thenReturn(model);
		final String currentHash = "current-hash";
		PersonHash storedHash = new PersonHash();
		storedHash.setHash(currentHash);
		assertNull(storedHash.getDateChanged());
		when(HashUtils.getStoredHash(eq(personUuid), any(Class.class), any(ProducerTemplate.class))).thenReturn(storedHash);
		when(HashUtils.computeHash(model)).thenReturn(storedHash.getHash());
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		producer.process(syncModel);
		
		verify(serviceFacade).delete(TableToSyncEnum.PERSON, personUuid);
		PowerMockito.verifyStatic(HashUtils.class);
		HashUtils.saveHash(storedHash, mockProducerTemplate, false);
		verify(mockLogger).debug("Updating hash for the deleted entity");
		assertEquals(HASH_DELETED, storedHash.getHash());
		assertNotNull(storedHash.getDateChanged());
	}
	
	@Test
	public void process_shouldInsertANewHashIfNoStoredHashIsFoundForAnExistingEntityGettingDeleted() throws Exception {
		final String personUuid = "some-uuid";
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		BaseModel model = new PersonModel();
		model.setUuid(personUuid);
		syncModel.setModel(model);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("d");
		syncModel.setMetadata(metadata);
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, personUuid)).thenReturn(model);
		PersonHash personHash = new PersonHash();
		assertNull(personHash.getIdentifier());
		assertNull(personHash.getHash());
		assertNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
		when(HashUtils.instantiateHashEntity(PersonHash.class)).thenReturn(personHash);
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		producer.process(syncModel);
		
		// Then
		PowerMockito.verifyStatic(HashUtils.class);
		HashUtils.saveHash(personHash, mockProducerTemplate, false);
		verify(mockLogger).info("Inserting new hash for the deleted entity with no existing hash");
		verify(mockLogger).debug("Saving new hash for the deleted entity");
		verify(mockLogger).debug("Successfully saved the new hash for the deleted entity");
		assertEquals(model.getUuid(), personHash.getIdentifier());
		assertEquals(HASH_DELETED, personHash.getHash());
		assertNotNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
		verify(serviceFacade).delete(TableToSyncEnum.PERSON, personUuid);
	}
	
	@Test
	public void process_ShouldPassIfTheExistingEntityFromTheDbForADeletedEntityHasADifferentHashFromTheStoredOne()
	    throws Exception {
		final String personUuid = "some-uuid";
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		BaseModel model = new PersonModel();
		model.setUuid(personUuid);
		syncModel.setModel(model);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("d");
		syncModel.setMetadata(metadata);
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, personUuid)).thenReturn(model);
		final String currentHash = "current-hash";
		PersonHash storedHash = new PersonHash();
		storedHash.setHash(currentHash);
		assertNull(storedHash.getDateChanged());
		when(HashUtils.getStoredHash(eq(personUuid), any(Class.class), any(ProducerTemplate.class))).thenReturn(storedHash);
		when(HashUtils.computeHash(model)).thenReturn("different-hash");
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		producer.process(syncModel);
		verify(mockLogger).debug("Updating hash for the deleted entity");
		verify(mockLogger).debug("Successfully updated the hash for the deleted entity");
		assertEquals(HASH_DELETED, storedHash.getHash());
		assertNotNull(storedHash.getDateChanged());
		verify(serviceFacade).delete(TableToSyncEnum.PERSON, personUuid);
	}
	
	@Test
	public void process_shouldUpdateTheStoredHashForADeletedEntityEvenWhenTheEntityIsAlreadyDeleted() {
		final String personUuid = "some-uuid";
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		BaseModel model = new PersonModel();
		model.setUuid(personUuid);
		syncModel.setModel(model);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("d");
		syncModel.setMetadata(metadata);
		final String currentHash = "current-hash";
		PersonHash storedHash = new PersonHash();
		storedHash.setHash(currentHash);
		assertNull(storedHash.getDateChanged());
		when(HashUtils.getStoredHash(eq(personUuid), any(Class.class), any(ProducerTemplate.class))).thenReturn(storedHash);
		when(HashUtils.computeHash(model)).thenReturn(storedHash.getHash());
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		producer.process(syncModel);
		
		verify(serviceFacade).delete(TableToSyncEnum.PERSON, personUuid);
		PowerMockito.verifyStatic(HashUtils.class);
		HashUtils.saveHash(storedHash, mockProducerTemplate, false);
		verify(mockLogger).info(
		    "Found existing hash for a missing entity, this could be a retry item to delete an entity but the hash was never updated to the terminal value");
		verify(mockLogger).debug("Updating hash for the deleted entity");
		assertEquals(HASH_DELETED, storedHash.getHash());
		assertNotNull(storedHash.getDateChanged());
	}
	
	@Test(expected = ConflictsFoundException.class)
	public void save_ShouldFailIfTheExistingEntityFromTheDbHasADifferentHashFromTheStoredOne() {
		// Given
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("u");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonModel dbModel = new PersonModel();
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, model.getUuid())).thenReturn(dbModel);
		PersonHash storedHash = new PersonHash();
		storedHash.setHash("old-hash");
		when(HashUtils.computeHash(dbModel)).thenReturn("new-hash");
		when(HashUtils.getStoredHash(model.getUuid(), PersonHash.class, mockProducerTemplate)).thenReturn(storedHash);
		
		// When
		producer.process(syncModel);
	}
	
	@Test
	public void save_ShouldIgnorePlaceHolderWhenCheckingForConflicts() {
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("u");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonModel dbModel = new PersonModel();
		dbModel.setVoided(true);
		dbModel.setVoidReason(DEFAULT_VOID_REASON);
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, model.getUuid())).thenReturn(dbModel);
		PersonHash storedHash = new PersonHash();
		storedHash.setHash("old-hash");
		final String expectedNewHash = "new-hash";
		when(HashUtils.computeHash(dbModel)).thenReturn("current-hash");
		when(HashUtils.computeHash(model)).thenReturn(expectedNewHash);
		when(HashUtils.getStoredHash(model.getUuid(), PersonHash.class, mockProducerTemplate)).thenReturn(storedHash);
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		// When
		producer.process(syncModel);
		verify(mockLogger).debug("Ignoring placeholder entity when checking for conflicts");
		
		// Then
		verify(serviceFacade).saveModel(TableToSyncEnum.PERSON, model);
		verify(mockLogger).debug("Updating hash for the incoming entity state");
		when(HashUtils.getStoredHash(model.getUuid(), PersonHash.class, mockProducerTemplate)).thenReturn(storedHash);
		assertEquals(expectedNewHash, storedHash.getHash());
		assertNotNull(storedHash.getDateChanged());
	}
	
	@Test
	public void process_shouldFailIfNoHashIsFoundForAnExistingEntityAndIgnoreIsNotSetToTrue() {
		// Given
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("u");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonModel dbModel = new PersonModel();
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, model.getUuid())).thenReturn(dbModel);
		expectedException.expect(EIPException.class);
		expectedException.expectMessage(equalTo("Failed to find the existing hash for an existing entity"));
		
		producer.process(syncModel);
	}
	
	@Test
	public void process_shouldInsertANewHashIfNoneIsFoundForAnExistingEntityAndIgnoreIsSetToTrue() throws Exception {
		// Given
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("u");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonModel dbModel = new PersonModel();
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, model.getUuid())).thenReturn(dbModel);
		PersonHash personHash = new PersonHash();
		assertNull(personHash.getIdentifier());
		assertNull(personHash.getHash());
		assertNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
		final String expectedHash = "testing";
		when(HashUtils.computeHash(model)).thenReturn(expectedHash);
		when(HashUtils.instantiateHashEntity(PersonHash.class)).thenReturn(personHash);
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		when(mockEnv.getProperty(Constants.PROP_IGNORE_MISSING_HASH)).thenReturn("true");
		
		// When
		producer.process(syncModel);
		
		// Then
		PowerMockito.verifyStatic(HashUtils.class);
		HashUtils.saveHash(personHash, mockProducerTemplate, false);
		verify(mockLogger).debug("Inserting new hash for existing entity with missing hash");
		verify(mockLogger).debug("Ignoring existing entity with missing hash when checking for conflicts");
		verify(mockLogger).debug("Saving new hash for the entity");
		verify(mockLogger).debug("Successfully saved new hash for the entity");
		assertEquals(model.getUuid(), personHash.getIdentifier());
		assertEquals(expectedHash, personHash.getHash());
		assertNotNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
		verify(serviceFacade).saveModel(TableToSyncEnum.PERSON, model);
	}
	
	@Test
	public void process_shouldNotFailIfNoHashIsFoundForAnExistingPlaceHolderEntity() throws Exception {
		// Given
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("u");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonModel dbModel = new PersonModel();
		dbModel.setVoided(true);
		dbModel.setVoidReason(DEFAULT_VOID_REASON);
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, model.getUuid())).thenReturn(dbModel);
		PersonHash personHash = new PersonHash();
		assertNull(personHash.getIdentifier());
		assertNull(personHash.getHash());
		assertNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
		final String expectedHash = "testing";
		when(HashUtils.computeHash(model)).thenReturn(expectedHash);
		when(HashUtils.instantiateHashEntity(PersonHash.class)).thenReturn(personHash);
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		producer.process(syncModel);
		
		PowerMockito.verifyStatic(HashUtils.class);
		HashUtils.saveHash(personHash, mockProducerTemplate, false);
		verify(mockLogger).debug("Inserting new hash for existing placeholder entity");
		verify(mockLogger).debug("Saving new hash for the entity");
		verify(mockLogger).debug("Successfully saved new hash for the entity");
		assertEquals(model.getUuid(), personHash.getIdentifier());
		assertEquals(expectedHash, personHash.getHash());
		assertNotNull(personHash.getDateCreated());
		assertNull(personHash.getDateChanged());
	}
	
	@Test
	public void process_ShouldPassIfTheDbEntityAndStoredHashDoNotMatchButDbEntityHashMatchesThatOfTheIncomingPayload() {
		PersonModel model = new PersonModel();
		model.setUuid("uuid");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setOperation("u");
		SyncModel syncModel = new SyncModel(PersonModel.class, model, metadata);
		PersonModel dbModel = new PersonModel();
		when(serviceFacade.getModel(TableToSyncEnum.PERSON, model.getUuid())).thenReturn(dbModel);
		PersonHash storedHash = new PersonHash();
		storedHash.setHash("old-hash");
		final String expectedNewHash = "new-hash";
		when(HashUtils.computeHash(dbModel)).thenReturn(expectedNewHash);
		when(HashUtils.computeHash(model)).thenReturn(expectedNewHash);
		when(HashUtils.getStoredHash(model.getUuid(), PersonHash.class, mockProducerTemplate)).thenReturn(storedHash);
		when(mockLogger.isDebugEnabled()).thenReturn(true);
		
		producer.process(syncModel);
		
		PowerMockito.verifyStatic(HashUtils.class);
		HashUtils.saveHash(storedHash, mockProducerTemplate, false);
		verify(mockLogger).info("Stored hash differs from that of the state in the DB, ignoring this because the "
		        + "incoming and DB states match");
		verify(mockLogger).debug("Updating hash for the incoming entity state");
		assertEquals(expectedNewHash, storedHash.getHash());
		assertNotNull(storedHash.getDateChanged());
	}
	
}
