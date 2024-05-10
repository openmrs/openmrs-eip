package org.openmrs.eip.app.management.repository;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class SenderReconcileRepositoryTest extends BaseSenderTest {
	
	@Autowired
	private SenderReconcileRepository repo;
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_sender_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getReconciliation_shouldGetTheReconciliation() {
		Assert.assertEquals(5L, repo.getReconciliation().getId().longValue());
	}
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_sender_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getTop3ByStatusOrderByDateCreatedDesc_shouldGetTheThreeMostRecentReconciliationsMatchingTheStatus() {
		List<SenderReconciliation> recs = repo.getTop3ByStatusOrderByDateCreatedDesc(SenderReconcileStatus.COMPLETED);
		Assert.assertEquals(3, recs.size());
		Assert.assertEquals(4L, recs.get(0).getId().longValue());
		Assert.assertEquals(3L, recs.get(1).getId().longValue());
		Assert.assertEquals(2L, recs.get(2).getId().longValue());
	}
	
}
