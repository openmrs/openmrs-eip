package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReceiverArchiveServiceTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverSyncArchiveRepository repo;
	
	@Autowired
	private ReceiverPrunedItemRepository prunedRepo;
	
	@Autowired
	private ReceiverArchiveService service;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void prune_shouldMoveAnArchiveToThePrunedQueue() {
		final Long id = 1L;
		ReceiverSyncArchive archive = repo.findById(id).get();
		assertEquals(0, prunedRepo.count());
		
		service.prune(archive);
		
		List<ReceiverPrunedItem> prunedItems = prunedRepo.findAll();
		assertEquals(1, prunedItems.size());
		assertEquals(archive.getMessageUuid(), prunedItems.get(0).getMessageUuid());
	}
	
}
