package org.openmrs.eip.web.receiver;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.management.hash.entity.PatientHash;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.utils.HashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ConflictControllerTest extends BaseReceiverTest {
	
	@Autowired
	private ConflictController controller;
	
	@Test
	public void shouldGetAllUnResolvedMessagesInTheConflictQueue() {
		Map result = controller.getAll();
		assertEquals(2, result.size());
		assertEquals(4, result.get("count"));
		assertEquals(4, ((List) result.get("items")).size());
	}
	
	@Test
	public void shouldGetTheConflictItemMatchingTheSpecifiedId() {
		assertEquals("2cfd940e-32dc-491f-8038-a8f3afe3e36d", ((ConflictQueueItem) controller.get(2L)).getMessageUuid());
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void shouldCopyTheConflictMessageToTheArchiveQueueAndMarkItAsResolved() throws Exception {
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		Assert.assertTrue(TestUtils.getEntities(ReceiverSyncArchive.class).isEmpty());
		ConflictQueueItem conflict = new ConflictQueueItem();
		conflict.setMessageUuid("message-uuid");
		conflict.setModelClassName(PatientModel.class.getName());
		conflict.setIdentifier(uuid);
		conflict.setEntityPayload("{}");
		conflict.setDateReceived(new Date());
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		conflict.setDateSentBySender(LocalDateTime.now());
		conflict.setSnapshot(true);
		conflict.setDateCreated(new Date());
		Assert.assertFalse(conflict.getResolved());
		TestUtils.saveEntity(conflict);
		
		BaseHashEntity hashEntity = new PatientHash();
		hashEntity.setIdentifier(uuid);
		final String currentHash = "current-hash";
		hashEntity.setHash(currentHash);
		hashEntity.setDateCreated(LocalDateTime.now());
		producerTemplate.sendBody("jpa:" + PatientHash.class.getSimpleName() + "?usePersist=true", hashEntity);
		Assert.assertNull(hashEntity.getDateChanged());
		
		controller.update(singletonMap("resolved", "true"), conflict.getId());
		
		conflict = TestUtils.getEntity(ConflictQueueItem.class, conflict.getId());
		Assert.assertTrue(conflict.getResolved());
		hashEntity = HashUtils.getStoredHash(uuid, PatientHash.class, producerTemplate);
		Assert.assertNotEquals(currentHash, hashEntity.getHash());
		Assert.assertNotNull(hashEntity.getDateChanged());
		List<ReceiverSyncArchive> archives = TestUtils.getEntities(ReceiverSyncArchive.class);
		assertEquals(1, archives.size());
        ReceiverSyncArchive archive = archives.get(0);
        assertEquals(conflict.getMessageUuid(), archive.getMessageUuid());
        assertEquals(conflict.getModelClassName(), archive.getModelClassName());
        assertEquals(conflict.getIdentifier(), archive.getIdentifier());
        assertEquals(conflict.getEntityPayload(), archive.getEntityPayload());
        assertEquals(conflict.getSite(), archive.getSite());
        assertEquals(conflict.getSnapshot(), archive.getSnapshot());
        assertEquals(conflict.getDateSentBySender(), archive.getDateSentBySender());
        assertEquals(conflict.getDateReceived(), archive.getDateReceived());
        assertNotNull(archive.getDateCreated());
	}
	
}
