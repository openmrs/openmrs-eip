package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.repository.PersonRepository;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:openmrs_core_data.sql")
public class PersonLoadTest extends BaseReceiverTest {
	
	@Autowired
	private PersonRepository personRepo;
	
	@Autowired
	private EntityLoader loader;
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	@Test
	public void load() {
		UserLight user = new UserLight();
		SyncContext.setAppUser(user);
		assertEquals(1, personRepo.findAll().size());
		
		loader.process(getPersonModel());
		
		assertEquals(2, personRepo.findAll().size());
	}
	
	private SyncModel getPersonModel() {
		return JsonUtils.unmarshalSyncModel("{" + "\"tableToSyncModelClass\":\"" + PersonModel.class.getName() + "\","
		        + "\"model\":{" + "\"uuid\":\"818b4ee6-8d68-4849-975d-80ab98016677\"," + "\"creatorUuid\":\""
		        + UserLight.class.getName() + "(1)\"," + "\"dateCreated\":\"2019-05-28T13:42:31+00:00\","
		        + "\"changedByUuid\":null," + "\"dateChanged\":null," + "\"voided\":false," + "\"voidedByUuid\":null,"
		        + "\"dateVoided\":null," + "\"voidReason\":null," + "\"gender\":\"F\"," + "\"birthdate\":\"1982-01-06\","
		        + "\"birthdateEstimated\":false," + "\"dead\":false," + "\"deathDate\":null," + "\"causeOfDeathUuid\":null,"
		        + "\"deathdateEstimated\":false," + "\"birthtime\":null" + "},\"metadata\":{\"operation\":\"c\"}" + "}");
	}
}
