package org.openmrs.eip.web.receiver;

import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.component.Constants.PLACEHOLDER_CLASS;
import static org.openmrs.eip.component.Constants.QUERY_SAVE_HASH;

import org.apache.camel.ProducerTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.openmrs.eip.component.utils.HashUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HashUtils.class)
public class ConflictControllerTest {
	
	private ConflictController controller;
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private EntityServiceFacade mockEntityServiceFacade;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(HashUtils.class);
		controller = new ConflictController();
		Whitebox.setInternalState(controller, ProducerTemplate.class, mockProducerTemplate);
		Whitebox.setInternalState(controller, EntityServiceFacade.class, mockEntityServiceFacade);
	}
	
	@Test
	public void update_shouldUpdateTheEntityHashWithThatOfTheLatestDatabaseState() throws Exception {
		final Integer conflictId = 1;
		final String personUuid = "uuid";
		TableToSyncEnum tableToSyncEnum = TableToSyncEnum.PERSON;
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setModelClassName(PersonModel.class.getName());
		conflict.setIdentifier(personUuid);
		Assert.assertFalse(conflict.getResolved());
		final Class clazz = ConflictQueueItem.class;
		when(mockProducerTemplate.requestBody("jpa:" + clazz.getSimpleName() + "?query=SELECT c FROM "
		        + clazz.getSimpleName() + " c WHERE c.id = " + conflictId,
		    null, clazz)).thenReturn(conflict);
		when(mockProducerTemplate.requestBody("jpa:" + clazz.getSimpleName(), conflict, clazz)).thenReturn(conflict);
		PersonModel dbModel = Mockito.mock(PersonModel.class);
		when(mockEntityServiceFacade.getModel(tableToSyncEnum, personUuid)).thenReturn(dbModel);
		PersonHash personHash = new PersonHash();
		Assert.assertNull(personHash.getHash());
		Assert.assertNull(personHash.getDateChanged());
		when(HashUtils.getStoredHash(personUuid, PersonHash.class, mockProducerTemplate)).thenReturn(personHash);
		final String newHash = "new-hash";
		when(HashUtils.computeHash(dbModel)).thenReturn(newHash);
		
		Assert.assertEquals(conflict, controller.update(singletonMap("resolved", "true"), conflictId));
		Assert.assertTrue(conflict.getResolved());
		Assert.assertEquals(newHash, personHash.getHash());
		Assert.assertNotNull(personHash.getHash());
		Assert.assertNotNull(personHash.getDateChanged());
		Mockito.verify(mockProducerTemplate)
		        .sendBody(QUERY_SAVE_HASH.replace(PLACEHOLDER_CLASS, PersonHash.class.getSimpleName()), personHash);
	}
	
}
