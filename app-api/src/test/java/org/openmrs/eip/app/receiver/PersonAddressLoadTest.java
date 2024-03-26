package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.repository.PersonAddressRepository;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "classpath:openmrs_core_data.sql")
public class PersonAddressLoadTest extends BaseReceiverTest {
	
	@Autowired
	private PersonAddressRepository addressRepo;
	
	@Autowired
	private EntityLoader loader;
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	@Test
	public void load() {
		UserLight user = new UserLight();
		user.setId(1L);
		SyncContext.setAppUser(user);
		assertEquals(0, addressRepo.findAll().size());
		
		loader.process(getAddressModel());
		
		assertEquals(1, addressRepo.findAll().size());
	}
	
	private SyncModel getAddressModel() {
		return JsonUtils.unmarshalSyncModel("{" + "\"tableToSyncModelClass\":\"" + PersonAddressModel.class.getName() + "\","
		        + "\"model\":{" + "\"uuid\":\"818b4ee6-8d68-4849-975d-80ab98016677\"," + "\"creatorUuid\":\""
		        + UserLight.class.getName() + "(1)\"," + "\"dateCreated\":\"2019-05-28T13:42:31+00:00\","
		        + "\"changedByUuid\":null," + "\"dateChanged\":null," + "\"voided\":false," + "\"voidedByUuid\":null,"
		        + "\"dateVoided\":null," + "\"voidReason\":null," + "\"address\":{" + "\"address1\":\"chemin perdu\"" + "}"
		        + "},\"metadata\":{\"operation\":\"c\"}" + "}");
	}
}
