package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ConflictServiceTest extends BaseReceiverTest {
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private ReceiverRetryRepository retryRepo;
	
	@Autowired
	private ConflictService service;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveToRetryQueue_shouldMoveTheConflictToTheRetryQueue() {
		final Long id = 1L;
		final String reason = "Moved from conflict queue due to a bad conflict";
		ConflictQueueItem conflict = conflictRepo.findById(id).get();
		assertEquals(0, retryRepo.count());
		
		ReceiverRetryQueueItem retry = service.moveToRetryQueue(conflict, reason);
		
		assertEquals(conflict.getMessageUuid(), retry.getMessageUuid());
		assertEquals(reason, retry.getMessage());
		assertEquals(1, retryRepo.count());
	}
	
}
