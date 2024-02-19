package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReceiverReconcileRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverReconcileRepository repo;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_reconciliation.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getReconciliation_shouldGetTheFirstIncompleteReconciliation() {
		List<ReceiverReconciliation> recs = repo.getReconciliation();
		Assert.assertEquals(1, recs.size());
		Assert.assertEquals(2L, recs.get(0).getId().longValue());
	}
	
}
