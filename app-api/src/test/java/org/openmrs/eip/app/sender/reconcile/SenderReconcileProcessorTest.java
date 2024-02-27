package org.openmrs.eip.app.sender.reconcile;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.DeletedEntityRepository;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.management.service.SenderReconcileService;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.jms.core.JmsTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppUtils.class)
public class SenderReconcileProcessorTest {
	
	@Mock
	private SenderReconcileService mockService;
	
	@Mock
	private SenderReconcileRepository mockRecRepo;
	
	@Mock
	private SenderTableReconcileRepository mockTableRecRepo;
	
	@Mock
	private DeletedEntityRepository deleteEntityRepo;
	
	@Mock
	private JmsTemplate mockJmsTemplate;
	
	private SenderReconcileProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(AppUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SenderReconcileProcessor(null, mockRecRepo, mockTableRecRepo, deleteEntityRepo, mockService,
		        mockJmsTemplate);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void processItem_shouldTakeSnapshotAndSaveItIfStatusIsNew() {
		SenderReconciliation rec = new SenderReconciliation();
		rec.setStatus(SenderReconcileStatus.NEW);
		List<SenderTableReconciliation> tableRecs = List.of(new SenderTableReconciliation());
		when(mockService.takeSnapshot()).thenReturn(tableRecs);
		
		processor.processItem(rec);
		
		Mockito.verify(mockService).saveSnapshot(rec, tableRecs);
	}
	
	@Test
	public void processItem_shouldFinalizeACompletedReconciliation() {
		SenderReconciliation rec = new SenderReconciliation();
		rec.setStatus(SenderReconcileStatus.PROCESSING);
		final String personTable = "person";
		final String visitTable = "visit";
		when(AppUtils.getTablesToSync()).thenReturn(Set.of(personTable, visitTable));
		SenderTableReconciliation personRec = Mockito.mock(SenderTableReconciliation.class);
		when(personRec.isCompleted()).thenReturn(true);
		SenderTableReconciliation visitRec = Mockito.mock(SenderTableReconciliation.class);
		when(visitRec.isCompleted()).thenReturn(true);
		when(mockTableRecRepo.getByTableNameIgnoreCase(personTable)).thenReturn(personRec);
		when(mockTableRecRepo.getByTableNameIgnoreCase(visitTable)).thenReturn(visitRec);
		
		processor.processItem(rec);
		
		assertEquals(SenderReconcileStatus.POST_PROCESSING, rec.getStatus());
		Mockito.verify(mockRecRepo).save(rec);
	}
	
	@Test
	public void processItem_shouldNotFinalizeANonCompletedReconciliation() {
		SenderReconciliation rec = new SenderReconciliation();
		rec.setStatus(SenderReconcileStatus.PROCESSING);
		final String personTable = "person";
		final String visitTable = "visit";
		when(AppUtils.getTablesToSync()).thenReturn(Set.of(personTable, visitTable));
		SenderTableReconciliation personRec = Mockito.mock(SenderTableReconciliation.class);
		when(personRec.isCompleted()).thenReturn(true);
		SenderTableReconciliation visitRec = Mockito.mock(SenderTableReconciliation.class);
		when(mockTableRecRepo.getByTableNameIgnoreCase(personTable)).thenReturn(personRec);
		when(mockTableRecRepo.getByTableNameIgnoreCase(visitTable)).thenReturn(visitRec);
		
		processor.processItem(rec);
		
		assertEquals(SenderReconcileStatus.PROCESSING, rec.getStatus());
		Mockito.verifyNoInteractions(mockRecRepo);
	}
	
	@Test
	public void processItem_shouldFailForACompletedReconciliation() {
		SenderReconciliation rec = new SenderReconciliation();
		rec.setStatus(SenderReconcileStatus.COMPLETED);
		EIPException ex = Assert.assertThrows(EIPException.class, () -> processor.processItem(rec));
		assertEquals("Reconciliation is already completed", ex.getMessage());
	}
	
}
