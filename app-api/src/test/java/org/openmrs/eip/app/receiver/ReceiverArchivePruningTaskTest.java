package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.DateUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, AppUtils.class, DateUtils.class })
public class ReceiverArchivePruningTaskTest {
	
	@Mock
	private ReceiverSyncArchiveRepository mockRepo;
	
	@Mock
	private Pageable mockPage;
	
	@Mock
	private Date mockMaxDate;
	
	private ReceiverArchivePruningTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DateUtils.class);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", true);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", false);
	}
	
	@Test
	public void getNextBatch_shouldReadTheNextPageOfArchivesToBePruned() {
		when(AppUtils.getTaskPage()).thenReturn(mockPage);
		final int maxAgeDays = 3;
		final List<Date> asOfDates = new ArrayList();
		when(DateUtils.subtractDays(any(Date.class), eq(maxAgeDays))).thenAnswer(invocation -> {
			asOfDates.add(invocation.getArgument(0));
			return mockMaxDate;
		});
		task = new ReceiverArchivePruningTask(maxAgeDays);
		setInternalState(task, ReceiverSyncArchiveRepository.class, mockRepo);
		Long timestamp = System.currentTimeMillis();
		
		task.getNextBatch();
		
		verify(mockRepo).findByDateCreatedLessThanEqual(mockMaxDate, mockPage);
		Date asOfDate = asOfDates.get(0);
		assertTrue(asOfDate.getTime() == timestamp || asOfDate.getTime() > timestamp);
	}
	
}
