package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.model.EncounterModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql({ "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
@Sql(scripts = "classpath:mgt_site_info.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverUtilsIntegrationTest extends BaseReceiverTest {
	
	private static final String PERSON_UUID = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
	
	@Autowired
	private SyncedMessageRepository syncedMsgRepo;
	
	@Test
	public void archiveMessage_shouldMoveAMessageFromTheSyncedToTheArchivesQueue() {
		assertTrue(TestUtils.getEntities(ReceiverSyncArchive.class).isEmpty());
		SyncedMessage syncedMsg = new SyncedMessage(SyncOutcome.SUCCESS);
		syncedMsg.setMessageUuid("some-msg-uuid");
		syncedMsg.setIdentifier("some-uuid");
		syncedMsg.setModelClassName(EncounterModel.class.getName());
		syncedMsg.setSnapshot(false);
		syncedMsg.setOperation(SyncOperation.c);
		syncedMsg.setDateSentBySender(LocalDateTime.now());
		syncedMsg.setSite(TestUtils.getEntity(SiteInfo.class, 1L));
		syncedMsg.setEntityPayload("{}");
		syncedMsg.setDateCreated(new Date());
		syncedMsg = syncedMsgRepo.save(syncedMsg);
		long timestamp = System.currentTimeMillis();
		
		ReceiverUtils.archiveMessage(syncedMsg);
		
		assertNull(TestUtils.getEntity(SyncedMessage.class, syncedMsg.getId()));
		List<ReceiverSyncArchive> archives = TestUtils.getEntities(ReceiverSyncArchive.class);
		assertEquals(1, archives.size());
		ReceiverSyncArchive a = archives.get(0);
		assertEquals(syncedMsg.getMessageUuid(), a.getMessageUuid());
		assertTrue(a.getDateCreated().getTime() == timestamp || a.getDateCreated().getTime() > timestamp);
	}
	
	@Test
	public void getPersonNameUuids_shouldReturnTheUuidsOfTheNamesOfThePersonWithTheSpecifiedUuid() {
		List<String> nameUuids = ReceiverUtils.getPersonNameUuids(PERSON_UUID);
		
		assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1bfd940e-32dc-491f-8038-a8f3afe3e35a"));
		assertTrue(nameUuids.contains("2bfd940e-32dc-491f-8038-a8f3afe3e35a"));
	}
	
	@Test
	public void getPatientIdentifierUuids_shouldReturnTheUuidsOfTheIdentifiersOfThePatientWithTheSpecifiedUuid() {
		List<String> nameUuids = ReceiverUtils.getPatientIdentifierUuids(PERSON_UUID);
		
		assertEquals(2, nameUuids.size());
		assertTrue(nameUuids.contains("1cfd940e-32dc-491f-8038-a8f3afe3e35c"));
		assertTrue(nameUuids.contains("2cfd940e-32dc-491f-8038-a8f3afe3e35c"));
	}
	
	@Test
	public void getPersonAttributeUuids_shouldReturnTheUuidsOfTheSearchableAttributesOfThePersonWithTheSpecifiedUuid() {
		List<String> attributeUuids = ReceiverUtils.getPersonAttributeUuids(PERSON_UUID);
		
		assertEquals(2, attributeUuids.size());
		assertTrue(attributeUuids.contains("2efd940e-32dc-491f-8038-a8f3afe3e35f"));
		assertTrue(attributeUuids.contains("4efd940e-32dc-491f-8038-a8f3afe3e35f"));
	}
	
	@Test
	public void updateColumn_shouldUpdateTheColumnValueInTheDatabase() {
		final Long id = 1L;
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, id);
		Assert.assertFalse(site.getDisabled());
		
		ReceiverUtils.updateColumn("site_info", "sync_disabled", id, true);
		
		assertTrue(TestUtils.getEntity(SiteInfo.class, id).getDisabled());
	}
	
}
