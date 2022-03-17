package org.openmrs.eip.component.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.BaseDbDrivenTest;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.repository.UserRepository;
import org.openmrs.eip.component.service.impl.PatientService;
import org.openmrs.eip.component.service.impl.PersonService;
import org.openmrs.eip.component.service.impl.UserService;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "classpath:test_data.sql")
public class AbstractEntityServiceIntegrationTest extends BaseDbDrivenTest {
	
	private static final String EXISTING_USER_UUID = "1a3b12d1-5c4f-415f-871b-b98a22137605";
	
	private AbstractEntityService personService;
	
	private AbstractEntityService patientService;
	
	private AbstractEntityService userService;
	
	private UserRepository userRepo;
	
	@Before
	public void setup() {
		personService = applicationContext.getBean(PersonService.class);
		patientService = applicationContext.getBean(PatientService.class);
		userService = applicationContext.getBean(UserService.class);
		userRepo = applicationContext.getBean(UserRepository.class);
		UserLight user = new UserLight();
		user.setId(1L);
		SyncContext.setAppUser(user);
	}
	
	@AfterClass
	public static void afterClass() {
		SyncContext.setAppUser(null);
	}
	
	/**
	 * This test ensures that we can sync a patient record for a patient created from an existing person
	 * record
	 */
	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void save_shouldSaveANewPatientRecordWhenThereIsAnExistingPersonRecord() {
		final String uuid = "ba3b12d1-5c4f-415f-871b-b98a22137604";
		assertNotNull(personService.getModel(uuid));
		Assert.assertNull(patientService.getModel(uuid));
		final int initialCount = patientService.getAllModels().size();
		PatientModel patientModel = new PatientModel();
		patientModel.setCreatorUuid(UserLight.class.getName() + "(1a3b12d1-5c4f-415f-871b-b98a22137605)");
		patientModel.setDateCreated(LocalDateTime.now());
		patientModel.setUuid(uuid);
		
		patientService.save(patientModel);
		
		assertNotNull(patientService.getModel(uuid));
		assertEquals(initialCount + 1, patientService.getAllModels().size());
	}
	
	@Test
	public void save_shouldSaveAUserWhereTheCreatorAndTheUserAreTheSame() {
		final String uuid = "ee3b12d2-5c4f-415f-871b-b98a22137605";
		Assert.assertNull(userService.getModel(uuid));
		final int initialCount = userService.getAllModels().size();
		UserModel user = new UserModel();
		user.setUsername("testing-user");
		user.setCreatorUuid(UserLight.class.getName() + "(" + uuid + ")");
		user.setDateCreated(LocalDateTime.now());
		user.setUuid(uuid);
		
		userService.save(user);
		
		assertNotNull(userService.getModel(uuid));
		assertEquals(initialCount + 1, userRepo.count());
	}
	
	@Test
	public void save_shouldSaveAUserWhereTheRetiredByFieldAndTheUserAreTheSame() {
		final String uuid = "fe3b12d2-5c4f-415f-871b-b98a22137606";
		Assert.assertNull(userService.getModel(uuid));
		final int initialCount = userService.getAllModels().size();
		UserModel user = new UserModel();
		user.setUsername("testing-user");
		user.setCreatorUuid(UserLight.class.getName() + "(" + EXISTING_USER_UUID + ")");
		user.setDateCreated(LocalDateTime.now());
		user.setRetired(true);
		user.setRetiredByUuid(UserLight.class.getName() + "(" + uuid + ")");
		user.setDateRetired(LocalDateTime.now());
		user.setRetireReason("Testing");
		user.setUuid(uuid);
		
		userService.save(user);
		
		assertNotNull(userService.getModel(uuid));
		assertEquals(initialCount + 1, userRepo.count());
	}
	
	@Test
	public void save_shouldSaveAUserWhereTheChangedByFieldAndTheUserAreTheSame() {
		final String uuid = "de3b12d2-5c4f-415f-871b-b98a22137607";
		Assert.assertNull(userService.getModel(uuid));
		final int initialCount = userService.getAllModels().size();
		UserModel user = new UserModel();
		user.setUsername("testing-user");
		user.setCreatorUuid(UserLight.class.getName() + "(" + EXISTING_USER_UUID + ")");
		user.setDateCreated(LocalDateTime.now());
		user.setChangedByUuid(UserLight.class.getName() + "(" + uuid + ")");
		user.setDateChanged(LocalDateTime.now());
		user.setUuid(uuid);
		
		userService.save(user);
		
		assertNotNull(userService.getModel(uuid));
		assertEquals(initialCount + 1, userRepo.count());
	}
	
}
