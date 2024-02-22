package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
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
	public void getByTableNameIgnoreCase_shouldReturnTheTableReconciliation() {
		Assert.assertEquals(2l, repo.getByTableNameIgnoreCase("visit").getId().longValue());
		Assert.assertEquals(2l, repo.getByTableNameIgnoreCase("VISIT").getId().longValue());
	}
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_sender_table_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getIncompleteReconciliations_shouldReturnTheIncompleteTableReconciliation() {
		List<SenderTableReconciliation> recs = repo.getIncompleteReconciliations();
		Assert.assertEquals(3, recs.size());
		Assert.assertEquals(1l, recs.get(0).getId().longValue());
		Assert.assertEquals(2l, recs.get(1).getId().longValue());
		Assert.assertEquals(4l, recs.get(2).getId().longValue());
	}
	
}
