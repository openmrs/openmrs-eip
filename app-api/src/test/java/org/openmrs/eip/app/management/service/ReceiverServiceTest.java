package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReceiverServiceTest extends BaseReceiverTest {
	
	@Autowired
	private SyncMessageRepository syncMsgRepo;
	
	@Autowired
	private SyncedMessageRepository syncedMsgRepo;
	
	@Autowired
	private ReceiverSyncArchiveRepository archiveRepo;
	
	@Autowired
	private ReceiverPrunedItemRepository prunedRepo;
	
	@Autowired
	private ReceiverService service;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void prune_shouldMoveAnArchiveToThePrunedQueue() {
		final Long id = 1L;
		ReceiverSyncArchive archive = archiveRepo.findById(id).get();
		assertEquals(0, prunedRepo.count());
		
		service.prune(archive);
		
		assertFalse(archiveRepo.findById(id).isPresent());
		List<ReceiverPrunedItem> prunedItems = prunedRepo.findAll();
		assertEquals(1, prunedItems.size());
		assertEquals(archive.getMessageUuid(), prunedItems.get(0).getMessageUuid());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveToSyncedQueue_shouldMoveTheMessageToTheSyncedQueue() {
		final Long id = 1L;
		SyncMessage msg = syncMsgRepo.findById(id).get();
		assertEquals(0, syncedMsgRepo.count());
		
		service.moveToSyncedQueue(msg, SyncOutcome.SUCCESS);
		
		assertFalse(syncMsgRepo.findById(id).isPresent());
		List<SyncedMessage> syncedItems = syncedMsgRepo.findAll();
		assertEquals(1, syncedItems.size());
		assertEquals(msg.getMessageUuid(), syncedItems.get(0).getMessageUuid());
		assertEquals(SyncOutcome.SUCCESS, syncedItems.get(0).getOutcome());
	}
	
}