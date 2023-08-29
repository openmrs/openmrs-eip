package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.receiver.ConflictServiceBehaviorTest.OPENMRS_PASS;
import static org.openmrs.eip.app.receiver.ConflictServiceBehaviorTest.OPENMRS_URL;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_CLEAR_CACHE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_DBSYNC;
import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_UPDATE_SEARCH_INDEX;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_USER;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.management.hash.entity.PatientHash;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.impl.PatientService;
import org.openmrs.eip.component.utils.HashUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(properties = Constants.PROP_OPENMRS_URL + "=" + OPENMRS_URL)
@TestPropertySource(properties = Constants.PROP_OPENMRS_PASS + "=" + OPENMRS_PASS)
@TestPropertySource(properties = "logging.level." + ROUTE_ID_CLEAR_CACHE + "=DEBUG")
@TestPropertySource(properties = "logging.level." + ROUTE_ID_DBSYNC + "=DEBUG")
@TestPropertySource(properties = "logging.level." + ROUTE_ID_CLEAR_CACHE + "=DEBUG")
@TestPropertySource(properties = "logging.level." + ROUTE_ID_UPDATE_SEARCH_INDEX + "=DEBUG")
public class ConflictServiceBehaviorTest extends BaseReceiverTest {
	
	protected static final String OPENMRS_URL = "mock:url";
	
	protected static final String OPENMRS_USER = "admin";
	
	protected static final String OPENMRS_PASS = "test";
	
	protected static final String CACHE_EVICT_URL = OPENMRS_URL + "/ws/rest/v1/cleardbcache?authMethod=Basic&authUsername="
	        + OPENMRS_USER + "&authPassword=" + OPENMRS_PASS;
	
	protected static final String SEARCH_INDEX_URL = OPENMRS_URL
	        + "/ws/rest/v1/searchindexupdate?authMethod=Basic&authUsername=" + OPENMRS_USER + "&authPassword="
	        + OPENMRS_PASS;
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private ReceiverSyncArchiveRepository archiveRepo;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private ConflictService service;
	
	@EndpointInject(CACHE_EVICT_URL)
	private MockEndpoint mockCacheEvictEndpoint;
	
	@EndpointInject(SEARCH_INDEX_URL)
	private MockEndpoint mockIndexUpdateEndpoint;
	
	@Before
	public void setup() throws Exception {
		mockCacheEvictEndpoint.reset();
		mockIndexUpdateEndpoint.reset();
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(env, PROP_OPENMRS_USER + "=" + OPENMRS_USER);
		loadXmlRoutes("receiver", "db-sync-route.xml", ROUTE_ID_UPDATE_SEARCH_INDEX + ".xml", ROUTE_ID_CLEAR_CACHE + ".xml");
	}
	
	@Test
	@Transactional(SyncConstants.CHAINED_TX_MGR)
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void resolveAsMerge_shouldMergeSyncAndArchiveTheConflict() throws Exception {
		final String msgUuid = "message-uuid";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String newGender = "F";
		final LocalDate newBirthDate = LocalDate.of(2023, Month.AUGUST, 1);
		assertTrue(conflictRepo.findAll().isEmpty());
		assertTrue(archiveRepo.findAll().isEmpty());
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setMessageUuid(msgUuid);
		conflict.setModelClassName(PatientModel.class.getName());
		conflict.setIdentifier(uuid);
		conflict.setOperation(SyncOperation.u);
		PatientModel newModel = new PatientModel();
		newModel.setUuid(uuid);
		newModel.setGender(newGender);
		newModel.setBirthdate(newBirthDate);
		newModel.setDead(true);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		SyncModel syncModel = SyncModel.builder().tableToSyncModelClass(PatientModel.class).model(newModel)
		        .metadata(metadata).build();
		conflict.setEntityPayload(JsonUtils.marshall(syncModel));
		conflict.setDateReceived(new Date());
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSnapshot(false);
		conflict.setDateCreated(new Date());
		conflictRepo.save(conflict);
		PatientHash hash = new PatientHash();
		hash.setDateCreated(LocalDateTime.now());
		hash.setHash("old-hash");
		hash.setIdentifier(uuid);
		HashUtils.saveHash(hash, producerTemplate, false);
		
		PatientModel existingPatient = patientService.getModel(uuid);
		assertNotEquals(newGender, existingPatient.getGender());
		assertNotEquals(newBirthDate, existingPatient.getBirthdate());
		assertFalse(existingPatient.isDead());
		Set<String> propsToSync = new HashSet<>();
		propsToSync.add("gender");
		propsToSync.add("birthdate");
		PatientModel expectedUpdatedModel = (PatientModel) BeanUtils.cloneBean(patientService.getModel(uuid));
		expectedUpdatedModel.setGender(newGender);
		expectedUpdatedModel.setBirthdate(newBirthDate);
		final String expectedNewHash = HashUtils.computeHash(expectedUpdatedModel);
		
		service.resolveAsMerge(conflict, propsToSync);
		
		existingPatient = patientService.getModel(uuid);
		assertEquals(newGender, existingPatient.getGender());
		assertEquals(newBirthDate, existingPatient.getBirthdate());
		assertFalse(existingPatient.isDead());
		assertEquals(expectedNewHash, HashUtils.getStoredHash(uuid, PatientHash.class, producerTemplate).getHash());
		assertTrue(conflictRepo.findAll().isEmpty());
		List<ReceiverSyncArchive> archives = archiveRepo.findAll();
		assertEquals(1, archiveRepo.findAll().size());
		assertEquals(conflict.getMessageUuid(), archives.get(0).getMessageUuid());
		mockCacheEvictEndpoint.assertIsSatisfied();
		mockIndexUpdateEndpoint.assertIsSatisfied();
	}
	
}
