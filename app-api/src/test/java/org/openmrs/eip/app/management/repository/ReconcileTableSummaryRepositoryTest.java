package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

public class ReconcileTableSummaryRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private ReconcileTableSummaryRepository repo;
	
	@Autowired
	private ReceiverReconcileRepository recRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_receiver_reconcile.sql",
	        "classpath:mgt_receiver_reconcile_table_summary.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getCountTotals_shouldGetTheCountTotalsForTheReconciliation() {
		List<Object[]> recTotals = repo.getCountTotals(recRepo.findById(1L).get());
		assertEquals(1, recTotals.size());
		Object[] totals = recTotals.get(0);
		assertEquals(11L, totals[0]);
		assertEquals(3L, totals[1]);
		assertEquals(4L, totals[2]);
		assertEquals(18L, totals[3]);
		assertEquals(7L, totals[4]);
		assertEquals(5L, totals[5]);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_receiver_reconcile.sql",
	        "classpath:mgt_receiver_reconcile_table_summary.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getCountTotalsBySite_shouldGetTheCountTotalsForTheReconciliationAndSite() {
		List<Object[]> recTotals = repo.getCountTotalsBySite(recRepo.findById(1L).get(), siteRepo.findById(1L).get());
		assertEquals(1, recTotals.size());
		Object[] totals = recTotals.get(0);
		assertEquals(8L, totals[0]);
		assertEquals(2L, totals[1]);
		assertEquals(3L, totals[2]);
		assertEquals(14L, totals[3]);
		assertEquals(6L, totals[4]);
		assertEquals(4L, totals[5]);
	}
	
}
