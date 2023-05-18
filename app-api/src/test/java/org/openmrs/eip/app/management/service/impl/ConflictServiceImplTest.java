package org.openmrs.eip.app.management.service.impl;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.component.management.hash.entity.PersonAddressHash;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.management.hash.entity.PersonNameHash;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.VisitModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HashUtils.class)
public class ConflictServiceImplTest {
	
	@Mock
	private ConflictRepository mockRepo;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private EntityServiceFacade mockServiceFacade;
	
	@Mock
	private Logger mockLogger;
	
	private ConflictServiceImpl service;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(HashUtils.class);
		service = new ConflictServiceImpl(mockRepo, mockServiceFacade, mockProducerTemplate);
		Whitebox.setInternalState(ConflictServiceImpl.class, Logger.class, mockLogger);
	}
	
	@Test
	public void getBadConflicts_shouldReturnConflictItemsWhereTheStoredAndDBEntityHashesMatch() {
		final String uuid1 = "uuid-1";
		final String uuid2 = "uuid-2";
		final String uuid3 = "uuid-3";
		final String uuid4 = "uuid-4";
		final String hash1 = "hash-1";
		final String hash3 = "hash-3";
		BaseModel model1 = new PersonModel();
		BaseModel model2 = new PersonNameModel();
		BaseModel model3 = new PersonAddressModel();
		BaseModel model4 = new PersonAttributeModel();
		ConflictQueueItem conflict1 = new ConflictQueueItem();
		conflict1.setIdentifier(uuid1);
		conflict1.setModelClassName(PersonModel.class.getName());
		ConflictQueueItem conflict2 = new ConflictQueueItem();
		conflict2.setIdentifier(uuid2);
		conflict2.setModelClassName(PersonNameModel.class.getName());
		ConflictQueueItem conflict3 = new ConflictQueueItem();
		conflict3.setIdentifier(uuid3);
		conflict3.setModelClassName(PersonAddressModel.class.getName());
		ConflictQueueItem conflict4 = new ConflictQueueItem();
		conflict4.setIdentifier(uuid4);
		conflict4.setModelClassName(PersonAttributeModel.class.getName());
		ConflictQueueItem conflict5 = new ConflictQueueItem();
		final Long id5 = 5L;
		conflict5.setId(id5);
		conflict5.setIdentifier("uuid-5");
		conflict5.setModelClassName(VisitModel.class.getName());
		List<ConflictQueueItem> conflicts = Arrays.asList(conflict1, conflict2, conflict3, conflict4, conflict5);
		when(mockRepo.findByResolvedIsFalse()).thenReturn(conflicts);
		when(mockServiceFacade.getModel(TableToSyncEnum.PERSON, uuid1)).thenReturn(model1);
		when(mockServiceFacade.getModel(TableToSyncEnum.PERSON_NAME, uuid2)).thenReturn(model2);
		when(mockServiceFacade.getModel(TableToSyncEnum.PERSON_ADDRESS, uuid3)).thenReturn(model3);
		when(mockServiceFacade.getModel(TableToSyncEnum.PERSON_ATTRIBUTE, uuid4)).thenReturn(model4);
		PersonHash hashObject1 = new PersonHash();
		hashObject1.setHash(hash1);
		PersonNameHash hashObject2 = new PersonNameHash();
		hashObject2.setHash("hash-2");
		PersonAddressHash hashObject3 = new PersonAddressHash();
		hashObject3.setHash(hash3);
		when(HashUtils.getStoredHash(uuid1, PersonHash.class, mockProducerTemplate)).thenReturn(hashObject1);
		when(HashUtils.getStoredHash(uuid2, PersonNameHash.class, mockProducerTemplate)).thenReturn(hashObject2);
		when(HashUtils.getStoredHash(uuid3, PersonAddressHash.class, mockProducerTemplate)).thenReturn(hashObject3);
		when(HashUtils.computeHash(model1)).thenReturn(hash1);
		when(HashUtils.computeHash(model2)).thenReturn("new-hash-2");
		when(HashUtils.computeHash(model3)).thenReturn(hash3);
		
		List<ConflictQueueItem> badConflicts = service.getBadConflicts();
		Assert.assertEquals(2, badConflicts.size());
		Assert.assertTrue(badConflicts.contains(conflict1));
		Assert.assertTrue(badConflicts.contains(conflict3));
		Mockito.verify(mockLogger).warn("No entity found in the database associated to conflict item with id: " + id5);
	}
	
}
