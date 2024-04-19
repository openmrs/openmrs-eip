package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class SiteReconciliationRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private SiteReconciliationRepository repo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_site_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getBySite_shouldGetTheReconciliationForTheSite() {
		Assert.assertEquals(3L, repo.getBySite(siteRepo.getReferenceById(2L)).getId().longValue());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_site_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void countByDateCompletedNotNull_shouldGetCountOfCompletedSiteReconciliations() {
		Assert.assertEquals(1, repo.countByDateCompletedNotNull());
	}
	
}
