package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation.ReconciliationStatus;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReceiverReconcileRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverReconcileRepository repo;
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_receiver_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getReconciliation_shouldGetTheFirstIncompleteReconciliation() {
		assertEquals(5L, repo.getReconciliation().getId().longValue());
	}
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_receiver_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getTop3ByStatusOrderByDateCreatedDesc_shouldGetTheThreeMostRecentReconciliationsMatchingTheStatus() {
		List<ReceiverReconciliation> recs = repo.getTop3ByStatusOrderByDateCreatedDesc(ReconciliationStatus.COMPLETED);
		assertEquals(3, recs.size());
		assertEquals(4L, recs.get(0).getId().longValue());
		assertEquals(3L, recs.get(1).getId().longValue());
		assertEquals(2L, recs.get(2).getId().longValue());
	}
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_receiver_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getByIdentifier_shouldGetTheReconciliationMatchingTheSpecifiedIdentifier() {
		assertEquals(2L, repo.getByIdentifier("rec-2").getId().longValue());
		Assert.assertNull(repo.getByIdentifier("rec-x"));
	}
	
}
