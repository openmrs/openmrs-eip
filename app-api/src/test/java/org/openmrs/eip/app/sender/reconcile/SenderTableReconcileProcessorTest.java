package org.openmrs.eip.app.sender.reconcile;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.SyncConstants.RECONCILE_MSG_SEPARATOR;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.sender.SenderUtils;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, SenderUtils.class })
public class SenderTableReconcileProcessorTest {
	
	private static final String QUEUE_NAME = "text_queue";
	
	private static final String RECONCILIATION_ID = "test_identifier";
	
	private static final int BATCH_SIZE = 5;
	
	@Mock
	private SenderTableReconcileRepository mockTableRecRepo;
	
	@Mock
	private SenderReconcileRepository mockRecRepo;
	
	@Mock
	private JmsTemplate mockJmsTemplate;
	
	@Mock
	private SyncEntityRepository mockOpenmrsRepo;
	
	@Mock
	private SenderReconciliation mockReconciliation;
	
	private SenderTableReconcileProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class, SenderUtils.class);
		when(SenderUtils.getQueueName()).thenReturn(QUEUE_NAME);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new SenderTableReconcileProcessor(null, BATCH_SIZE, mockTableRecRepo, mockRecRepo, mockJmsTemplate);
		when(mockRecRepo.getReconciliation()).thenReturn(mockReconciliation);
		when(mockReconciliation.getIdentifier()).thenReturn(RECONCILIATION_ID);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	private SenderTableReconciliation createReconciliation(String table, long rowCount, long endId, long lastProcId,
	                                                       boolean started) {
		SenderTableReconciliation rec = new SenderTableReconciliation();
		rec.setTableName(table);
		rec.setRowCount(rowCount);
		rec.setEndId(endId);
		rec.setLastProcessedId(lastProcId);
		rec.setStarted(started);
		return rec;
	}
	
	@Test
	public void processItem_shouldProcessBatchWithNoRowsRead() {
		final String table = "person";
		final long rowCount = 10;
		final long endId = 20;
		SenderTableReconciliation rec = createReconciliation(table, rowCount, endId, 20, false);
		LocalDateTime snapshotDate = LocalDateTime.now();
		rec.setSnapshotDate(snapshotDate);
		Assert.assertFalse(rec.isStarted());
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockOpenmrsRepo);
		
		processor.processItem(rec);
		
		Mockito.verify(mockTableRecRepo).save(rec);
		Assert.assertTrue(rec.isStarted());
		assertEquals(endId, rec.getLastProcessedId());
		ReconciliationResponse expectedResp = new ReconciliationResponse();
		expectedResp.setIdentifier(RECONCILIATION_ID);
		expectedResp.setTableName(table);
		expectedResp.setRemoteStartDate(snapshotDate);
		expectedResp.setRowCount(rowCount);
		expectedResp.setBatchSize(0);
		expectedResp.setLastTableBatch(true);
		expectedResp.setData("");
		Mockito.verify(mockJmsTemplate).convertAndSend(QUEUE_NAME, JsonUtils.marshall(expectedResp));
	}
	
	@Test
	public void processItem_shouldReadAndSubmitABatchOfUuids() {
		final String table = "person";
		final long rowCount = 20;
		final long endId = 25;
		final long lastProcId = 10;
		SenderTableReconciliation rec = createReconciliation(table, rowCount, endId, lastProcId, true);
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockOpenmrsRepo);
		Pageable page = Pageable.ofSize(BATCH_SIZE);
		List<Object[]> batch = new ArrayList<>();
		batch.add(new Object[] { "uuid-11", 11L });
		batch.add(new Object[] { "uuid-12", 12L });
		batch.add(new Object[] { "uuid-13", 13L });
		batch.add(new Object[] { "uuid-14", 14L });
		batch.add(new Object[] { "uuid-15", 15L });
		when(mockOpenmrsRepo.getUuidAndIdBatchToReconcile(lastProcId, endId, page)).thenReturn(batch);
		
		processor.processItem(rec);
		
		Mockito.verify(mockTableRecRepo).save(rec);
		assertEquals(15L, rec.getLastProcessedId());
		ReconciliationResponse expectedResp = new ReconciliationResponse();
		expectedResp.setIdentifier(RECONCILIATION_ID);
		expectedResp.setTableName(table);
		expectedResp.setBatchSize(batch.size());
		List<String> uuids = batch.stream().map(entry -> entry[0].toString()).collect(Collectors.toList());
		expectedResp.setData(StringUtils.join(uuids, RECONCILE_MSG_SEPARATOR));
		Mockito.verify(mockJmsTemplate).convertAndSend(QUEUE_NAME, JsonUtils.marshall(expectedResp));
	}
	
	@Test
	public void processItem_shouldFinalizeReconciliation() {
		final String table = "person";
		final long rowCount = 15;
		final long endId = 15;
		final long lastProcId = 10;
		SenderTableReconciliation rec = createReconciliation(table, rowCount, endId, lastProcId, true);
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockOpenmrsRepo);
		Pageable page = Pageable.ofSize(BATCH_SIZE);
		List<Object[]> batch = new ArrayList<>();
		batch.add(new Object[] { "uuid-11", 11L });
		batch.add(new Object[] { "uuid-12", 12L });
		batch.add(new Object[] { "uuid-13", 13L });
		batch.add(new Object[] { "uuid-14", 14L });
		batch.add(new Object[] { "uuid-15", 15L });
		when(mockOpenmrsRepo.getUuidAndIdBatchToReconcile(lastProcId, endId, page)).thenReturn(batch);
		
		processor.processItem(rec);
		
		Mockito.verify(mockTableRecRepo).save(rec);
		assertEquals(15L, rec.getLastProcessedId());
		ReconciliationResponse expectedResp = new ReconciliationResponse();
		expectedResp.setIdentifier(RECONCILIATION_ID);
		expectedResp.setTableName(table);
		expectedResp.setBatchSize(batch.size());
		expectedResp.setLastTableBatch(true);
		List<String> uuids = batch.stream().map(entry -> entry[0].toString()).collect(Collectors.toList());
		expectedResp.setData(StringUtils.join(uuids, RECONCILE_MSG_SEPARATOR));
		Mockito.verify(mockJmsTemplate).convertAndSend(QUEUE_NAME, JsonUtils.marshall(expectedResp));
	}
	
}
