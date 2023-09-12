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

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.management.hash.entity.PatientHash;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Ignore
@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ConflictControllerIntegrationTest extends BaseReceiverTest {
	
	@Autowired
	private ConflictController controller;
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private ReceiverSyncArchiveRepository archiveRepo;
	
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
		HashUtils.saveHash(hashEntity, producerTemplate, false);
		
		controller.delete(conflict.getId());
		
		assertFalse(conflictRepo.findById(conflict.getId()).isPresent());
		assertEquals(1, archiveRepo.count());
	}
	
}
