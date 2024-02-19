package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class SenderReconcileRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private SenderReconcileRepository repo;
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_sender_reconciliation.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getReconciliation_shouldGetTheReconciliation() {
		Assert.assertEquals(1L, repo.getReconciliation().getId().longValue());
	}
	
}
