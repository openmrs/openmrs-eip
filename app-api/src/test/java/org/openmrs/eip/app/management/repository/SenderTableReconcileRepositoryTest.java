package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class SenderTableReconcileRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private SenderTableReconcileRepository repo;
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_sender_table_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getByTableName_shouldReturnTheTableReconciliation() {
		Assert.assertEquals(2l, repo.getByTableName("visit").getId().longValue());
	}
	
}
