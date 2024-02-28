package org.openmrs.eip.app.sender.reconcile;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.SyncConstants.RECONCILE_MSG_SEPARATOR;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.sender.DeletedEntity;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.DeletedEntityRepository;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.management.service.SenderReconcileService;
import org.openmrs.eip.app.sender.SenderUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.jms.core.JmsTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppUtils.class, SenderUtils.class })
public class SenderReconcileProcessorTest {
	
	private static final String QUEUE_NAME = "test";
	
	private static final String RECONCILE_ID = "testId";
	
	private static final String SITE_ID = "siteId";
	
	@Mock
	private SenderReconcileService mockService;
	
	@Mock
	private SenderReconcileRepository mockRecRepo;
	
	@Mock
	private SenderTableReconcileRepository mockTableRecRepo;
	
	@Mock
	private DeletedEntityRepository mockDeleteRepo;
	
	@Mock
	private JmsTemplate mockJmsTemplate;
	
	private SenderReconcileProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(SenderUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		Mockito.when(SenderUtils.getQueueName()).thenReturn(QUEUE_NAME);
		processor = new SenderReconcileProcessor(null, mockRecRepo, mockTableRecRepo, mockDeleteRepo, mockService,
		        mockJmsTemplate);
		Whitebox.setInternalState(processor, "siteId", SITE_ID);
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
		
		verify(mockService).saveSnapshot(rec, tableRecs);
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
		verify(mockRecRepo).save(rec);
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
	
	@Test
	public void processItem_shouldPostProcessTheReconciliation() {
		final String personTable = "person";
		final String visitTable = "visit";
		final Long personId1 = 1L;
		final Long personId2 = 2L;
		final Long personId3 = 3L;
		final String personUuid1 = "person-uuid1";
		final String personUuid2 = "person-uuid2";
		final Date recDate = new Date();
		SenderReconciliation rec = new SenderReconciliation();
		rec.setIdentifier(RECONCILE_ID);
		rec.setStatus(SenderReconcileStatus.POST_PROCESSING);
		rec.setDateCreated(recDate);
		SenderTableReconciliation personRec = new SenderTableReconciliation();
		personRec.setTableName(personTable);
		personRec.setEndId(personId2);
		SenderTableReconciliation visitRec = new SenderTableReconciliation();
		visitRec.setTableName(visitTable);
		when(mockTableRecRepo.findAll()).thenReturn(List.of(personRec, visitRec));
		DeletedEntity deletedPerson1 = new DeletedEntity();
		deletedPerson1.setPrimaryKeyId(personId1.toString());
		deletedPerson1.setIdentifier(personUuid1);
		DeletedEntity deletedPerson2 = new DeletedEntity();
		deletedPerson2.setPrimaryKeyId(personId2.toString());
		deletedPerson2.setIdentifier(personUuid2);
		DeletedEntity deletedPerson3 = new DeletedEntity();
		deletedPerson3.setPrimaryKeyId(personId3.toString());
		when(mockDeleteRepo.getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual(personTable, recDate))
		        .thenReturn(List.of(deletedPerson1, deletedPerson2, deletedPerson3));
		
		processor.processItem(rec);
		
		ArgumentCaptor<ReconcileResponseCreator> creatorArgCaptor = ArgumentCaptor.forClass(ReconcileResponseCreator.class);
		verify(mockJmsTemplate).send(eq(QUEUE_NAME), creatorArgCaptor.capture());
		ReconcileResponseCreator creator = creatorArgCaptor.getValue();
		ReconciliationResponse response = new ReconciliationResponse();
		response.setIdentifier(RECONCILE_ID);
		response.setTableName(personTable);
		response.setBatchSize(2);
		response.setData(StringUtils.join(List.of(personUuid1, personUuid2), RECONCILE_MSG_SEPARATOR));
		assertEquals(JsonUtils.marshall(response), creator.getBody());
		assertEquals(SITE_ID, creator.getSiteId());
		verify(mockDeleteRepo).getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual(visitTable, recDate);
	}
	
}
