package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

public class SenderReconcileServiceTest extends BaseSenderTest {
	
	@Autowired
	private SenderReconcileService service;
	
	@Autowired
	private SenderTableReconcileRepository repo;
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void takeSnapshot_shouldTakeInitialSnapshotForExistingRowsForEachSyncedTable() {
		assertEquals(0, repo.count());
		long timestamp = System.currentTimeMillis();
		
		List<SenderTableReconciliation> recs = service.takeSnapshot();
		
		assertEquals(AppUtils.getTablesToSync().size(), recs.size());
		recs.forEach(r -> {
			if ("person".equalsIgnoreCase(r.getTableName())) {
				assertEquals(2, r.getRowCount());
				assertEquals(101, r.getEndId());
			} else if ("person_name".equalsIgnoreCase(r.getTableName())) {
				assertEquals(2, r.getRowCount());
				assertEquals(2, r.getEndId());
			} else if ("person_attribute".equalsIgnoreCase(r.getTableName())) {
				assertEquals(4, r.getRowCount());
				assertEquals(4, r.getEndId());
			} else if ("patient".equalsIgnoreCase(r.getTableName())) {
				assertEquals(1, r.getRowCount());
				assertEquals(101, r.getEndId());
			} else if ("patient_identifier".equalsIgnoreCase(r.getTableName())) {
				assertEquals(2, r.getRowCount());
				assertEquals(2, r.getEndId());
			} else {
				assertEquals(0, r.getRowCount());
				assertEquals(0, r.getEndId());
			}
			
			assertEquals(0, r.getLastProcessedId());
			long startDateMillis = r.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			assertTrue(startDateMillis == timestamp || startDateMillis > timestamp);
			assertTrue(r.getDateCreated().getTime() == timestamp || r.getDateCreated().getTime() > timestamp);
		});
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void takeSnapshot_shouldTakeIncrementalSnapshotForExistingRowsForEachSyncedTable() {
		assertEquals(0, repo.count());
		List<SenderTableReconciliation> recs = service.takeSnapshot();
		assertEquals(AppUtils.getTablesToSync().size(), recs.size());
		service.saveTableReconciliations(recs);
		Map<String, Date> tableDateCreatedMap = recs.stream().collect(
		    Collectors.toMap(SenderTableReconciliation::getTableName, SenderTableReconciliation::getDateCreated));
		final long personLastProcId = 101;
		SenderTableReconciliation personRec = repo.getByTableNameIgnoreCase("person");
		personRec.setLastProcessedId(personLastProcId);
		repo.save(personRec);
		long timestamp = System.currentTimeMillis();
		
		recs = service.takeSnapshot();
		
		assertEquals(AppUtils.getTablesToSync().size(), recs.size());
		recs.forEach(r -> {
			if ("person".equalsIgnoreCase(r.getTableName())) {
				assertEquals(0, r.getRowCount());
				assertEquals(101, r.getEndId());
			} else if ("person_name".equalsIgnoreCase(r.getTableName())) {
				assertEquals(2, r.getRowCount());
				assertEquals(2, r.getEndId());
			} else if ("person_attribute".equalsIgnoreCase(r.getTableName())) {
				assertEquals(4, r.getRowCount());
				assertEquals(4, r.getEndId());
			} else if ("patient".equalsIgnoreCase(r.getTableName())) {
				assertEquals(1, r.getRowCount());
				assertEquals(101, r.getEndId());
			} else if ("patient_identifier".equalsIgnoreCase(r.getTableName())) {
				assertEquals(2, r.getRowCount());
				assertEquals(2, r.getEndId());
			} else {
				assertEquals(0, r.getRowCount());
				assertEquals(0, r.getEndId());
			}
			
			if ("person".equalsIgnoreCase(r.getTableName())) {
				assertEquals(personLastProcId, r.getLastProcessedId());
			} else {
				assertEquals(0, r.getLastProcessedId());
			}
			
			long startDateMillis = r.getStartDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			assertTrue(startDateMillis == timestamp || startDateMillis > timestamp);
			assertEquals(tableDateCreatedMap.get(r.getTableName()), r.getDateCreated());
		});
	}
	
}
