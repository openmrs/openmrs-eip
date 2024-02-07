package org.openmrs.eip.app.receiver;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.SyncConstants.RECONCILE_MSG_SEPARATOR;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.service.ReconcileService;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class ReconcileMessageProcessorTest {
	
	private static final int MAX_REC_BATCH_SIZE = 100;
	
	private ReconcileMessageProcessor processor;
	
	@Mock
	private SyncEntityRepository mockEntityRepo;
	
	@Mock
	private ReconcileService mockService;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ReconcileMessageProcessor(null, mockService);
		Whitebox.setInternalState(processor, "maxReconcileBatchSize", MAX_REC_BATCH_SIZE);
		Whitebox.setInternalState(processor, "minReconcileBatchSize", 2);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void processItem_shouldReconcileTheUuidsInTheMessage() {
		final int uuidSize = 100;
		final String table = "person";
		List<String> uuids = rangeClosed(1, uuidSize).boxed().map(i -> "uuid-" + i).toList();
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setTableName(table);
		msg.setData(StringUtils.join(uuids, RECONCILE_MSG_SEPARATOR));
		msg.setBatchSize(uuidSize);
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockEntityRepo);
		when(mockEntityRepo.countByUuidIn(uuids)).thenReturn(uuidSize);
		
		processor.processItem(msg);
		
		verify(mockService).updateReconciliationMessage(msg, true, uuids);
	}
	
	@Test
	public void processItem_shouldBisectTheUuidsIfTheSizeIsLargerThanTheMaxThreshold() {
		final String table = "person";
		List<String> uuids = rangeClosed(1, 40).boxed().map(i -> "uuid-" + i).toList();
		List<String> batch1 = rangeClosed(1, 10).boxed().map(i -> "uuid-" + i).toList();
		List<String> batch2 = rangeClosed(11, 20).boxed().map(i -> "uuid-" + i).toList();
		List<String> batch3 = rangeClosed(21, 30).boxed().map(i -> "uuid-" + i).toList();
		List<String> batch4 = rangeClosed(31, 40).boxed().map(i -> "uuid-" + i).toList();
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setTableName(table);
		msg.setData(StringUtils.join(uuids, RECONCILE_MSG_SEPARATOR));
		msg.setBatchSize(uuids.size());
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockEntityRepo);
		when(mockEntityRepo.countByUuidIn(batch1)).thenReturn(batch1.size());
		when(mockEntityRepo.countByUuidIn(batch2)).thenReturn(batch2.size());
		when(mockEntityRepo.countByUuidIn(batch3)).thenReturn(batch3.size());
		when(mockEntityRepo.countByUuidIn(batch4)).thenReturn(batch4.size());
		Whitebox.setInternalState(processor, "maxReconcileBatchSize", 10);
		
		processor.processItem(msg);
		
		verify(mockService).updateReconciliationMessage(msg, true, batch1);
		verify(mockService).updateReconciliationMessage(msg, true, batch2);
		verify(mockService).updateReconciliationMessage(msg, true, batch3);
		verify(mockService).updateReconciliationMessage(msg, true, batch4);
	}
	
	@Test
	public void processItem_shouldProcessACountOfUuidsWhereSubListsHaveAnOddLength() {
		final String table = "person";
		final int batchSize = 70;
		List<String> uuids = rangeClosed(1, batchSize).boxed().map(i -> "uuid-" + i).toList();
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setTableName(table);
		msg.setData(StringUtils.join(uuids, RECONCILE_MSG_SEPARATOR));
		msg.setBatchSize(uuids.size());
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockEntityRepo);
		final List<String> processedUuids = new ArrayList<>(batchSize);
		doAnswer(invocation -> {
			List<String> uuidsArg = invocation.getArgument(0);
			processedUuids.addAll(uuidsArg);
			return uuidsArg.size();
		}).when(mockEntityRepo).countByUuidIn(anyList());
		
		Whitebox.setInternalState(processor, "maxReconcileBatchSize", 10);
		
		processor.processItem(msg);
		
		assertTrue(uuids.equals(processedUuids));
		verify(mockService, times(8)).updateReconciliationMessage(eq(msg), eq(true), anyList());
	}
	
	@Test
	public void processItem_shouldBisectAndCheckAsLongAsThereAreMissingUuidsUntilMinThresholdIsReached() {
		final String table = "person";
		final int minBatchSize = 5;
		final int batchSize = 63;
		Whitebox.setInternalState(processor, "maxReconcileBatchSize", batchSize);
		Whitebox.setInternalState(processor, "minReconcileBatchSize", minBatchSize);
		List<String> uuids = rangeClosed(1, batchSize).boxed().map(i -> "uuid-" + i).toList();
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setTableName(table);
		msg.setData(StringUtils.join(uuids, RECONCILE_MSG_SEPARATOR));
		msg.setBatchSize(uuids.size());
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockEntityRepo);
		final List<String> processedUuids = new ArrayList<>(batchSize);
		doAnswer(invocation -> {
			List<String> uuidsArg = invocation.getArgument(0);
			//If sublist is on the left side of the initial list
			if (uuidsArg.get(0).equals("uuid-" + 1)) {
				//We have reached a batch size smaller than min threshold report all as missing uuids
				if (uuidsArg.size() < minBatchSize) {
					processedUuids.addAll(uuidsArg);
					return 0;
				}
				
				//mock one missing uuid
				return uuidsArg.size() - 1;
			}
			
			processedUuids.addAll(uuidsArg);
			return uuidsArg.size();
		}).when(mockEntityRepo).countByUuidIn(anyList());
		
		processor.processItem(msg);
		
		assertTrue(uuids.equals(processedUuids));
		verify(mockService, times(4)).updateReconciliationMessage(eq(msg), eq(true), anyList());
		verify(mockService).updateReconciliationMessage(eq(msg), eq(false), anyList());
	}
	
	@Test
	public void processItem_shouldProcessItemsOneByOneIfBatchHasMissingUuidsAndSizeIsLessThanMinThreshold() {
		final String table = "person";
		final int minBatchSize = 10;
		final int batchSize = 5;
		Whitebox.setInternalState(processor, "minReconcileBatchSize", minBatchSize);
		final List<String> missingUuids = List.of("uuid-2", "uuid-4");
		List<String> uuids = rangeClosed(1, batchSize).boxed().map(i -> "uuid-" + i).toList();
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setTableName(table);
		msg.setData(StringUtils.join(uuids, RECONCILE_MSG_SEPARATOR));
		msg.setBatchSize(uuids.size());
		when(SyncContext.getRepositoryBean(table)).thenReturn(mockEntityRepo);
		final List<String> processedUuids = new ArrayList<>(batchSize);
		when(mockEntityRepo.countByUuidIn(anyList())).thenReturn(1);
		
		doAnswer(invocation -> {
			String uuidArg = invocation.getArgument(0);
			processedUuids.add(uuidArg);
			return !missingUuids.contains(uuidArg);
		}).when(mockEntityRepo).existsByUuid(anyString());
		
		processor.processItem(msg);
		
		assertTrue(uuids.equals(processedUuids));
		verify(mockService, times(3)).updateReconciliationMessage(eq(msg), eq(true), anyList());
		verify(mockService, times(2)).updateReconciliationMessage(eq(msg), eq(false), anyList());
	}
	
}
