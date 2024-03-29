package org.openmrs.eip.app.receiver.task;

import static java.util.Collections.synchronizedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.receiver.task.ReceiverRetryTask.BATCH_SIZE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.model.VisitModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, AppUtils.class })
public class ReceiverRetryTaskTest {
	
	@Mock
	private ReceiverRetryRepository mockRepo;
	
	private ReceiverRetryTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(AppUtils.class);
		when(SyncContext.getBean(ReceiverRetryRepository.class)).thenReturn(mockRepo);
		task = new ReceiverRetryTask(null);
	}
	
	@After
	public void tearDown() {
		setFailures(synchronizedSet(new HashSet<>()));
		setRetryIds(null);
	}
	
	private Set<String> getFailures() {
		return Whitebox.getInternalState(ReceiverRetryTask.class, "FAILED_ENTITIES");
	}
	
	private void setFailures(Set<String> failures) {
		Whitebox.setInternalState(ReceiverRetryTask.class, "FAILED_ENTITIES", failures);
	}
	
	private List<Long> getRetryIds() {
		return Whitebox.getInternalState(ReceiverRetryTask.class, "retryIds");
	}
	
	private void setRetryIds(List<Long> ids) {
		Whitebox.setInternalState(ReceiverRetryTask.class, "retryIds", ids);
	}
	
	@Test
	public void beforeStart_shouldLoadRetryIdsAndResetFailuresList() {
		setFailures(new HashSet<>(Set.of("test")));
		Assert.assertNull(getRetryIds());
		List<Long> ids = List.of(2L, 3L);
		when(mockRepo.getIds()).thenReturn(ids);
		
		task.beforeStart();
		
		assertTrue(getFailures().isEmpty());
		assertEquals(ids, getRetryIds());
	}
	
	@Test
	public void postProcess_shouldRemoveTheRetryId() {
		setRetryIds(new ArrayList<>(List.of(2L, 3L)));
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setId(2L);
		
		task.postProcess(retry, false);
		
		assertEquals(List.of(3L), getRetryIds());
	}
	
	@Test
	public void postProcess_shouldRemoveTheRetryIdAndAddItemToFailuresIfAnErrorIsEncountered() {
		final String uuid = "test-uuid";
		final String modelClass = VisitModel.class.getName();
		setRetryIds(new ArrayList<>(List.of(2L, 3L)));
		ReceiverRetryQueueItem retry = new ReceiverRetryQueueItem();
		retry.setIdentifier(uuid);
		retry.setModelClassName(modelClass);
		retry.setId(2L);
		
		task.postProcess(retry, true);
		
		assertTrue(getFailures().contains(modelClass + "#" + uuid));
		assertEquals(List.of(3L), getRetryIds());
	}
	
	@Test
	public void beforeStop_shouldClearRetryIds() {
		setRetryIds(new ArrayList<>(List.of(2L, 3L)));
		setFailures(new HashSet<>(Set.of("test")));
		
		task.beforeStop();
		
		assertTrue(getRetryIds().isEmpty());
	}
	
	@Test
	public void getNextBatch_shouldReturnAnEmptyListIfRetryIdsIsEmpty() {
		setRetryIds(Collections.emptyList());
		assertTrue(task.getNextBatch().isEmpty());
	}
	
	@Test
	public void getNextBatch_shouldFetchTheNextBatchOfRetries() {
		List<Long> ids = List.of(2L, 3L);
		setRetryIds(new ArrayList<>(ids));
		List<ReceiverRetryQueueItem> retries = List.of(new ReceiverRetryQueueItem(), new ReceiverRetryQueueItem());
		when(mockRepo.getByIdInOrderByDateReceivedAsc(ids)).thenReturn(retries);
		assertEquals(retries, task.getNextBatch());
	}
	
	@Test
	public void getNextBatch_shouldFetchTheNextBatchOfRetriesIfIdsIsMoreThanTheBatchSize() {
		setRetryIds(new ArrayList<>(LongStream.rangeClosed(1, 201).boxed().toList()));
		List<ReceiverRetryQueueItem> retries = List.of(new ReceiverRetryQueueItem(), new ReceiverRetryQueueItem(),
		    new ReceiverRetryQueueItem());
		when(mockRepo.getByIdInOrderByDateReceivedAsc(LongStream.rangeClosed(1, BATCH_SIZE).boxed().toList()))
		        .thenReturn(retries);
		assertEquals(retries, task.getNextBatch());
	}
	
}
