package org.openmrs.eip.app.management.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.repository.PersonRepository;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppUtils.class, SyncContext.class })
public class SenderReconcileServiceImplTest {
	
	@Mock
	private SenderTableReconcileRepository mockTableRecRepo;
	
	@Mock
	private PersonRepository mockPersonRepo;
	
	private SenderReconcileServiceImpl service;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(SyncContext.class);
		service = new SenderReconcileServiceImpl(null, mockTableRecRepo);
	}
	
	@Test
	public void takeSnapshot_shouldResetForAnEmptyTable() {
		final String table = "person";
		when(AppUtils.getTablesToSync()).thenReturn(Set.of(table));
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockPersonRepo);
		
		List<SenderTableReconciliation> recs = service.takeSnapshot();
		
		assertEquals(1, recs.size());
		SenderTableReconciliation rec = recs.get(0);
		assertEquals(table, rec.getTableName());
		assertEquals(0, rec.getRowCount());
		assertEquals(0, rec.getEndId());
		assertEquals(0, rec.getLastProcessedId());
	}
	
	@Test
	public void takeSnapshot_shouldResetForAnEmptyTableIfRowsWereDeleted() {
		final String table = "person";
		when(AppUtils.getTablesToSync()).thenReturn(Set.of(table));
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockPersonRepo);
		SenderTableReconciliation existingRec = new SenderTableReconciliation();
		when(mockTableRecRepo.getByTableNameIgnoreCase(table)).thenReturn(existingRec);
		
		List<SenderTableReconciliation> recs = service.takeSnapshot();
		
		assertEquals(1, recs.size());
		assertEquals(existingRec, recs.get(0));
		assertEquals(0, existingRec.getRowCount());
		assertEquals(0, existingRec.getEndId());
		assertEquals(0, existingRec.getLastProcessedId());
	}
	
	@Test
	public void takeSnapshot_shouldProcessInitialSnapshotForTableWithRows() {
		final String table = "person";
		final long expectedRowCount = 7;
		final long maxId = 10;
		when(AppUtils.getTablesToSync()).thenReturn(Set.of(table));
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockPersonRepo);
		SenderTableReconciliation existingRec = new SenderTableReconciliation();
		when(mockTableRecRepo.getByTableNameIgnoreCase(table)).thenReturn(existingRec);
		when(mockPersonRepo.getMaxId()).thenReturn(maxId);
		when(mockPersonRepo.countByIdLessThanEqual(maxId)).thenReturn(expectedRowCount);
		
		List<SenderTableReconciliation> recs = service.takeSnapshot();
		
		assertEquals(1, recs.size());
		assertEquals(existingRec, recs.get(0));
		assertEquals(expectedRowCount, existingRec.getRowCount());
		assertEquals(maxId, existingRec.getEndId());
		assertEquals(0, existingRec.getLastProcessedId());
	}
	
	@Test
	public void takeSnapshot_shouldProcessIncrementalSnapshotForTableWithRows() {
		final String table = "person";
		final long expectedRowCount = 7;
		final long maxId = 17;
		final long lastProcessedId = 10;
		when(AppUtils.getTablesToSync()).thenReturn(Set.of(table));
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockPersonRepo);
		SenderTableReconciliation existingRec = new SenderTableReconciliation();
		existingRec.setStarted(true);
		existingRec.setLastProcessedId(lastProcessedId);
		when(mockTableRecRepo.getByTableNameIgnoreCase(table)).thenReturn(existingRec);
		when(mockPersonRepo.getMaxId()).thenReturn(maxId);
		when(mockPersonRepo.countByIdLessThanEqual(maxId)).thenReturn(expectedRowCount);
		
		List<SenderTableReconciliation> recs = service.takeSnapshot();
		
		assertEquals(1, recs.size());
		assertEquals(existingRec, recs.get(0));
		assertFalse(existingRec.isStarted());
		assertEquals(expectedRowCount, existingRec.getRowCount());
		assertEquals(maxId, existingRec.getEndId());
		assertEquals(0, existingRec.getLastProcessedId());
	}
	
}
