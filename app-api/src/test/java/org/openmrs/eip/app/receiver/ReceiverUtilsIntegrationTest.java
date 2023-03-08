package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.EncounterModel;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverUtilsIntegrationTest extends BaseReceiverTest {
	
	@Autowired
	private SyncedMessageRepository syncedMsgRepo;
	
	private SyncedMessage createMessage(Class<? extends BaseModel> modelClass) {
		SyncedMessage syncedMsg = new SyncedMessage();
		syncedMsg.setMessageUuid("some-msg-uuid");
		syncedMsg.setIdentifier("some-uuid");
		syncedMsg.setModelClassName(modelClass.getName());
		syncedMsg.setSnapshot(false);
		syncedMsg.setOperation(SyncOperation.c);
		syncedMsg.setDateSentBySender(LocalDateTime.now());
		syncedMsg.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		syncedMsg.setEntityPayload("{}");
		syncedMsg.setItemized(false);
		syncedMsg.setDateCreated(new Date());
		syncedMsg = syncedMsgRepo.save(syncedMsg);
		assertFalse(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertNull(syncedMsg.getCached());
		assertNull(syncedMsg.getEvictedFromCache());
		assertNull(syncedMsg.getIndexed());
		assertNull(syncedMsg.getSearchIndexUpdated());
		
		return syncedMsg;
	}
	
	@Test
	public void itemize_shouldItemizeASyncedMessageForAPerson() {
		SyncedMessage syncedMsg = createMessage(PersonModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertTrue(syncedMsg.getCached());
		assertFalse(syncedMsg.getEvictedFromCache());
		assertTrue(syncedMsg.getIndexed());
		assertFalse(syncedMsg.getSearchIndexUpdated());
	}
	
	@Test
	public void itemize_shouldItemizeASyncedMessageForAPatient() {
		SyncedMessage syncedMsg = createMessage(PatientModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertTrue(syncedMsg.getCached());
		assertFalse(syncedMsg.getEvictedFromCache());
		assertTrue(syncedMsg.getIndexed());
		assertFalse(syncedMsg.getSearchIndexUpdated());
	}
	
	@Test
	public void itemize_shouldItemizeASyncedMessageForAPersonName() {
		SyncedMessage syncedMsg = createMessage(PersonNameModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertTrue(syncedMsg.getCached());
		assertFalse(syncedMsg.getEvictedFromCache());
		assertTrue(syncedMsg.getIndexed());
		assertFalse(syncedMsg.getSearchIndexUpdated());
	}
	
	@Test
	public void itemize_shouldItemizeASyncedMessageForAPersonAttribute() {
		SyncedMessage syncedMsg = createMessage(PersonAttributeModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertTrue(syncedMsg.getCached());
		assertFalse(syncedMsg.getEvictedFromCache());
		assertTrue(syncedMsg.getIndexed());
		assertFalse(syncedMsg.getSearchIndexUpdated());
	}
	
	@Test
	public void itemize_shouldItemizeASyncedMessageForAPatientIdentifier() {
		SyncedMessage syncedMsg = createMessage(PatientIdentifierModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertTrue(syncedMsg.getIndexed());
		assertFalse(syncedMsg.getSearchIndexUpdated());
		assertFalse(syncedMsg.getCached());
		assertNull(syncedMsg.getEvictedFromCache());
	}
	
	@Test
	public void itemize_shouldItemizeASyncedMessageForAPersonAddress() {
		SyncedMessage syncedMsg = createMessage(PersonAddressModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertTrue(syncedMsg.getCached());
		assertFalse(syncedMsg.getEvictedFromCache());
		assertFalse(syncedMsg.getIndexed());
		assertNull(syncedMsg.getSearchIndexUpdated());
	}
	
	@Test
	public void itemize_itemize_shouldItemizeASyncedMessageForAUser() {
		SyncedMessage syncedMsg = createMessage(UserModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertTrue(syncedMsg.getCached());
		assertFalse(syncedMsg.getEvictedFromCache());
		assertFalse(syncedMsg.getIndexed());
		assertNull(syncedMsg.getSearchIndexUpdated());
	}
	
	@Test
	public void itemize_shouldItemizeASyncedMessageForNonCachedAndNonIndexedType() {
		SyncedMessage syncedMsg = createMessage(EncounterModel.class);
		
		ReceiverUtils.itemize(syncedMsg);
		syncedMsg = TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId());
		
		assertTrue(syncedMsg.isItemized());
		assertFalse(syncedMsg.isResponseSent());
		assertFalse(syncedMsg.getCached());
		assertNull(syncedMsg.getEvictedFromCache());
		assertFalse(syncedMsg.getIndexed());
		assertNull(syncedMsg.getSearchIndexUpdated());
	}
	
	@Test
	public void archiveMessage_shouldMoveAMessageFromTheSyncedToTheArchivesQueue() {
		assertTrue(TestUtils.getEntities(ReceiverSyncArchive.class).isEmpty());
		SyncedMessage syncedMsg = createMessage(EncounterModel.class);
		long timestamp = System.currentTimeMillis();
		
		ReceiverUtils.archiveMessage(syncedMsg);
		
		assertNull(TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId()));
		List<ReceiverSyncArchive> archives = TestUtils.getEntities(ReceiverSyncArchive.class);
		assertEquals(1, archives.size());
		ReceiverSyncArchive a = archives.get(0);
		assertEquals(syncedMsg.getMessageUuid(), a.getMessageUuid());
		assertTrue(a.getDateCreated().getTime() > timestamp);
	}
	
}
