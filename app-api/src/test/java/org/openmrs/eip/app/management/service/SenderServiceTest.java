package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderPrunedArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.repository.SenderPrunedArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderSyncArchiveRepository;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class SenderServiceTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncArchiveRepository archiveRepo;
	
	@Autowired
	private SenderPrunedArchiveRepository prunedRepo;
	
	@Autowired
	private SenderService service;
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_archive.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void prune_shouldMoveAnArchiveToThePrunedTable() {
		final Long id = 1L;
		SenderSyncArchive archive = archiveRepo.findById(id).get();
		assertEquals(0, prunedRepo.count());
		
		service.prune(archive);
		
		assertFalse(archiveRepo.findById(id).isPresent());
		List<SenderPrunedArchive> prunedItems = prunedRepo.findAll();
		assertEquals(1, prunedItems.size());
		assertEquals(archive.getMessageUuid(), prunedItems.get(0).getMessageUuid());
	}
	
}
