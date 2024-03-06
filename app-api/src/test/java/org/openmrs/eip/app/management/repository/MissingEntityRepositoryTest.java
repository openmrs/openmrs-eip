package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_missing_entity.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class MissingEntityRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private MissingEntityRepository repo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Test
	public void countBySiteAndTableNameIgnoreCase_shouldGetTheCountOfMissingEntities() {
		SiteInfo site = siteRepo.getReferenceById(1L);
		assertEquals(3, repo.countBySiteAndTableNameIgnoreCase(site, "VISIT"));
	}
	
	@Test
	public void countBySiteAndTableNameIgnoreCaseAndInSyncQueueTrue_shouldGetTheCountOfMatchesInTheSyncQueue() {
		SiteInfo site = siteRepo.getReferenceById(1L);
		assertEquals(1, repo.countBySiteAndTableNameIgnoreCaseAndInSyncQueueTrue(site, "VISIT"));
	}
	
	@Test
	public void countBySiteAndTableNameIgnoreCaseAndInErrorQueueTrue_shouldGetTheCountOfMatchesInTheErrorQueue() {
		SiteInfo site = siteRepo.getReferenceById(1L);
		assertEquals(1, repo.countBySiteAndTableNameIgnoreCaseAndInErrorQueueTrue(site, "VISIT"));
	}
	
}
