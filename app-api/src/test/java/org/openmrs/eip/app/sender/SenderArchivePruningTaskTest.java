package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.repository.SenderSyncArchiveRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.DateUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, AppUtils.class, DateUtils.class })
public class SenderArchivePruningTaskTest {
	
	@Mock
	private SenderSyncArchiveRepository mockRepo;
	
	@Mock
	private Pageable mockPage;
	
	@Mock
	private Date mockMaxDate;
	
	private SenderArchivePruningTask task;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DateUtils.class);
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
		task = new SenderArchivePruningTask(maxAgeDays);
		setInternalState(task, SenderSyncArchiveRepository.class, mockRepo);
		Long timestamp = System.currentTimeMillis();
		
		task.getNextBatch();
		
		verify(mockRepo).findByDateCreatedLessThanEqual(mockMaxDate, mockPage);
		Date asOfDate = asOfDates.get(0);
		assertTrue(asOfDate.getTime() == timestamp || asOfDate.getTime() > timestamp);
	}
	
}
