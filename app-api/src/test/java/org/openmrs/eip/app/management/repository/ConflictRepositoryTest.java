package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_conflict_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ConflictRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ConflictRepository repo;
	
	@Test
	public void findByResolvedIsFalse_shouldGetAllUnresolvedConflicts() {
		List<ConflictQueueItem> conflicts = repo.findByResolvedIsFalse();
		assertEquals(4, conflicts.size());
		assertEquals(1, conflicts.get(0).getId().intValue());
		assertEquals(2, conflicts.get(1).getId().intValue());
		assertEquals(3, conflicts.get(2).getId().intValue());
		assertEquals(5, conflicts.get(3).getId().intValue());
	}
	
}
