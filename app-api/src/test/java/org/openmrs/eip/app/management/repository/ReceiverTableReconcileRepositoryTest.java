package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReceiverTableReconcileRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverTableReconcileRepository repo;
	
	@Autowired
	private SiteReconciliationRepository siteRecRepo;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_site_reconcile.sql",
	        "classpath:mgt_receiver_table_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getBySiteReconciliationAndTableName_shouldGetTheReconciliationForTheSite() {
		SiteReconciliation siteRec = siteRecRepo.getReferenceById(2L);
		Assert.assertEquals(2L, repo.getBySiteReconciliationAndTableName(siteRec, "person").getId().longValue());
	}
}
