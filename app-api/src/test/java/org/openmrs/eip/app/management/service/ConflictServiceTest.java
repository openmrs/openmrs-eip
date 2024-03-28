package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.management.hash.entity.PatientHash;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.service.impl.PatientService;
import org.openmrs.eip.component.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ConflictServiceTest extends BaseReceiverTest {
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private ReceiverRetryRepository retryRepo;
	
	@Autowired
	private ReceiverSyncArchiveRepository archiveRepo;
	
	@Autowired
	private ConflictService service;
	
	@Autowired
	private PatientService patientService;
	
	@Test
	public void moveToRetryQueue_shouldMoveTheConflictToTheRetryQueue() {
		final Long id = 1L;
		final String reason = "Moved from conflict queue due to a bad conflict";
		ConflictQueueItem conflict = conflictRepo.findById(id).get();
		assertEquals(0, retryRepo.count());
		
		ReceiverRetryQueueItem retry = service.moveToRetryQueue(conflict, reason);
		
		assertFalse(conflictRepo.findById(id).isPresent());
		assertEquals(conflict.getMessageUuid(), retry.getMessageUuid());
		assertEquals(reason, retry.getMessage());
		assertEquals(1, retryRepo.count());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void moveToArchiveQueue_shouldMoveTheConflictToTheArchiveQueueAndUpdateTheHash() {
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
		final String expectedNewHash = HashUtils.computeHash(patientService.getModel(uuid));
		final String currentHash = "current-hash";
		assertNotEquals(currentHash, expectedNewHash);
		hashEntity.setHash(currentHash);
		hashEntity.setDateCreated(LocalDateTime.now());
		HashUtils.saveHash(hashEntity, producerTemplate, false);
		Assert.assertNull(hashEntity.getDateChanged());
		final long timestamp = System.currentTimeMillis();
		
		ReceiverSyncArchive archive = service.moveToArchiveQueue(conflict);
		
		assertFalse(conflictRepo.findById(conflict.getId()).isPresent());
		assertEquals(conflict.getMessageUuid(), archive.getMessageUuid());
		assertEquals(1, archiveRepo.count());
		hashEntity = HashUtils.getStoredHash(uuid, PatientHash.class, producerTemplate);
		assertEquals(expectedNewHash, hashEntity.getHash());
		long dateChangedMillis = hashEntity.getDateChanged().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		assertTrue(dateChangedMillis == timestamp || dateChangedMillis > timestamp);
	}
	
	@Test
	public void hasConflictItem_shouldReturnTrueIfAnEntityHasAConflict() {
		assertTrue(service.hasConflictItem("uuid-2", PersonModel.class.getName()));
		assertTrue(service.hasConflictItem("uuid-2", PatientModel.class.getName()));
	}
	
	@Test
	public void hasConflictItem_shouldReturnFalseIfAnEntityHasNoConflict() {
		assertFalse(service.hasConflictItem("some-uuid", PersonModel.class.getName()));
	}
	
}
