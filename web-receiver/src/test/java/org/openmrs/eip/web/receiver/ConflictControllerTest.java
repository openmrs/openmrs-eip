package org.openmrs.eip.web.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.camel.OpenmrsLoadProducer;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.management.hash.entity.PatientHash;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.service.impl.PersonService;
import org.openmrs.eip.component.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ConflictControllerTest extends BaseReceiverTest {
	
	@Autowired
	private ConflictController controller;
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private ReceiverRetryRepository retryRepo;
	
	@Autowired
	private ReceiverSyncArchiveRepository archiveRepo;
	
	@Autowired
	private PersonService personService;
	
	@Test
	public void shouldGetAllUnResolvedMessagesInTheConflictQueue() {
		Map result = controller.getAll();
		assertEquals(2, result.size());
		assertEquals(5, result.get("count"));
		assertEquals(5, ((List) result.get("items")).size());
	}
	
	@Test
	public void shouldGetTheConflictItemMatchingTheSpecifiedId() {
		assertEquals("2cfd940e-32dc-491f-8038-a8f3afe3e36d", ((ConflictQueueItem) controller.get(2L)).getMessageUuid());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void delete_shouldMoveTheConflictToTheArchiveQueue() {
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		assertTrue(conflictRepo.findAll().isEmpty());
		assertTrue(archiveRepo.findAll().isEmpty());
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setMessageUuid("message-uuid");
		conflict.setModelClassName(PatientModel.class.getName());
		conflict.setIdentifier(uuid);
		conflict.setOperation(SyncOperation.c);
		conflict.setEntityPayload("{}");
		conflict.setDateReceived(new Date());
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSnapshot(false);
		conflict.setDateCreated(new Date());
		conflictRepo.save(conflict);
		
		BaseHashEntity hashEntity = new PatientHash();
		hashEntity.setIdentifier(uuid);
		hashEntity.setHash("current-hash");
		hashEntity.setDateCreated(LocalDateTime.now());
		OpenmrsLoadProducer.saveHash(hashEntity, producerTemplate, false);
		
		controller.delete(conflict.getId());
		
		assertFalse(conflictRepo.findById(conflict.getId()).isPresent());
		assertEquals(1, archiveRepo.count());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void getCountOfConflictsWithValidHashes_shouldGetAllConflictsWithValidHashesToTheRetryQueue() {
		final String uuid = "1b3b12d1-5c4f-415f-871b-b98a22137605";
		final String msgUuid = "message-uuid";
		Assert.assertEquals(0, conflictRepo.count());
		Assert.assertEquals(0, retryRepo.count());
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setMessageUuid(msgUuid);
		conflict.setModelClassName(PersonModel.class.getName());
		conflict.setIdentifier(uuid);
		conflict.setOperation(SyncOperation.c);
		conflict.setEntityPayload("{}");
		conflict.setDateReceived(new Date());
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSnapshot(true);
		conflict.setDateCreated(new Date());
		TestUtils.saveEntity(conflict);
		Assert.assertEquals(1, conflictRepo.count());
		
		BaseHashEntity hashEntity = new PersonHash();
		hashEntity.setIdentifier(uuid);
		hashEntity.setHash(HashUtils.computeHash(personService.getModel(uuid)));
		hashEntity.setDateCreated(LocalDateTime.now());
		OpenmrsLoadProducer.saveHash(hashEntity, producerTemplate, false);
		Assert.assertNull(hashEntity.getDateChanged());
		
		Assert.assertEquals(1, controller.verify());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void clean_shouldMoveAllConflictsWithValidHashesToTheRetryQueue() {
		final String uuid = "1b3b12d1-5c4f-415f-871b-b98a22137605";
		final String msgUuid = "message-uuid";
		Assert.assertEquals(0, conflictRepo.count());
		Assert.assertEquals(0, retryRepo.count());
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setMessageUuid(msgUuid);
		conflict.setModelClassName(PersonModel.class.getName());
		conflict.setIdentifier(uuid);
		conflict.setOperation(SyncOperation.c);
		conflict.setEntityPayload("{}");
		conflict.setDateReceived(new Date());
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSnapshot(true);
		conflict.setDateCreated(new Date());
		TestUtils.saveEntity(conflict);
		Assert.assertEquals(1, conflictRepo.count());
		
		BaseHashEntity hashEntity = new PersonHash();
		hashEntity.setIdentifier(uuid);
		hashEntity.setHash(HashUtils.computeHash(personService.getModel(uuid)));
		hashEntity.setDateCreated(LocalDateTime.now());
		OpenmrsLoadProducer.saveHash(hashEntity, producerTemplate, false);
		Assert.assertNull(hashEntity.getDateChanged());
		
		Assert.assertEquals(1, controller.clean());
		
		Assert.assertEquals(0, conflictRepo.count());
		Assert.assertEquals(1, retryRepo.count());
		Assert.assertEquals(msgUuid, retryRepo.findAll().get(0).getMessageUuid());
	}
	
}
