package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
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
	private SenderTableReconcileRepository tableRecRepo;
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void takeSnapshot_shouldTakeInitialSnapshotForExistingRowsForEachSyncedTable() {
		assertEquals(0, tableRecRepo.count());
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
			long startDateMillis = r.getSnapshotDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			assertTrue(startDateMillis == timestamp || startDateMillis > timestamp);
			assertTrue(r.getDateCreated().getTime() == timestamp || r.getDateCreated().getTime() > timestamp);
		});
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void takeSnapshot_shouldTakeIncrementalSnapshotForExistingRowsForEachSyncedTable() {
		assertEquals(0, tableRecRepo.count());
		List<SenderTableReconciliation> recs = service.takeSnapshot();
		assertEquals(AppUtils.getTablesToSync().size(), recs.size());
		service.saveTableReconciliations(recs);
		Map<String, Date> tableDateCreatedMap = recs.stream().collect(
		    Collectors.toMap(SenderTableReconciliation::getTableName, SenderTableReconciliation::getDateCreated));
		final long personLastProcId = 101;
		SenderTableReconciliation personRec = tableRecRepo.getByTableNameIgnoreCase("person");
		personRec.setLastProcessedId(personLastProcId);
		tableRecRepo.save(personRec);
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
			
			long startDateMillis = r.getSnapshotDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
			assertTrue(startDateMillis == timestamp || startDateMillis > timestamp);
			assertEquals(tableDateCreatedMap.get(r.getTableName()), r.getDateCreated());
		});
	}
	
	@Test
	public void saveTableReconciliations_shouldSaveAllTheReconciliations() {
		assertEquals(0, tableRecRepo.count());
		SenderTableReconciliation rec1 = new SenderTableReconciliation();
		rec1.setTableName("person");
		rec1.setDateCreated(new Date());
		rec1.setLastProcessedId(0);
		rec1.setRowCount(0);
		rec1.setEndId(1);
		rec1.setSnapshotDate(LocalDateTime.now());
		SenderTableReconciliation rec2 = new SenderTableReconciliation();
		rec2.setTableName("visit");
		rec2.setDateCreated(new Date());
		rec2.setLastProcessedId(0);
		rec2.setRowCount(0);
		rec2.setEndId(1);
		rec2.setSnapshotDate(LocalDateTime.now());
		
		service.saveTableReconciliations(List.of(rec1, rec2));
		
		assertEquals(2, tableRecRepo.count());
	}
	
}
