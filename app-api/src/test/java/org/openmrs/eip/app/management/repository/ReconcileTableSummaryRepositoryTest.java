package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReconcileTableSummary;
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
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_receiver_reconcile.sql",
	        "classpath:mgt_receiver_reconcile_table_summary.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getReport_shouldGetTheReportForTheReconciliation() {
		List<Object[]> report = repo.getReport(recRepo.findById(1L).get());
		assertEquals(3, report.size());
		Object[] patientTotals = report.get(0);
		assertEquals("patient", patientTotals[0]);
		assertEquals(0L, patientTotals[1]);
		assertEquals(0L, patientTotals[2]);
		assertEquals(0L, patientTotals[3]);
		assertEquals(0L, patientTotals[4]);
		assertEquals(0L, patientTotals[5]);
		assertEquals(0L, patientTotals[6]);
		Object[] personTotals = report.get(1);
		assertEquals("person", personTotals[0]);
		assertEquals(5L, personTotals[1]);
		assertEquals(1L, personTotals[2]);
		assertEquals(2L, personTotals[3]);
		assertEquals(9L, personTotals[4]);
		assertEquals(4L, personTotals[5]);
		assertEquals(3L, personTotals[6]);
		Object[] personNameTotals = report.get(2);
		assertEquals("person_name", personNameTotals[0]);
		assertEquals(6L, personNameTotals[1]);
		assertEquals(2L, personNameTotals[2]);
		assertEquals(2L, personNameTotals[3]);
		assertEquals(9L, personNameTotals[4]);
		assertEquals(3L, personNameTotals[5]);
		assertEquals(2L, personNameTotals[6]);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql", "classpath:mgt_receiver_reconcile.sql",
	        "classpath:mgt_receiver_reconcile_table_summary.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getByReconciliationAndSite_shouldGetTheSummariesForTheReconciliationAndSite() {
		List<ReconcileTableSummary> summaries = repo.getByReconciliationAndSite(recRepo.findById(1L).get(),
		    siteRepo.findById(1L).get());
		assertEquals(3, summaries.size());
		ReconcileTableSummary summary = summaries.get(0);
		assertEquals("person", summary.getTableName());
		assertEquals(5L, summary.getMissingCount());
		assertEquals(1L, summary.getMissingSyncCount());
		assertEquals(2L, summary.getMissingErrorCount());
		assertEquals(9L, summary.getUndeletedCount());
		assertEquals(4L, summary.getUndeletedSyncCount());
		assertEquals(3L, summary.getUndeletedErrorCount());
		summary = summaries.get(1);
		assertEquals("patient", summary.getTableName());
		assertEquals(0L, summary.getMissingCount());
		assertEquals(0L, summary.getMissingSyncCount());
		assertEquals(0L, summary.getMissingErrorCount());
		assertEquals(0L, summary.getUndeletedCount());
		assertEquals(0L, summary.getUndeletedSyncCount());
		assertEquals(0L, summary.getUndeletedErrorCount());
		summary = summaries.get(2);
		assertEquals("person_name", summary.getTableName());
		assertEquals(3L, summary.getMissingCount());
		assertEquals(1L, summary.getMissingSyncCount());
		assertEquals(1L, summary.getMissingErrorCount());
		assertEquals(5L, summary.getUndeletedCount());
		assertEquals(2L, summary.getUndeletedSyncCount());
		assertEquals(1L, summary.getUndeletedErrorCount());
	}
	
}
