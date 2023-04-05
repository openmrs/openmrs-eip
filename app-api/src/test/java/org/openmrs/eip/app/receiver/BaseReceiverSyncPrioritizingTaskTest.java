package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask.DEFAULT_BACK_LOG;
import static org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask.DEFAULT_SIZE_REFRESH_INTERVAL;
import static org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask.DEFAULT_SYNC_TIME_PER_ITEM;
import static org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask.KEY_SYNC_COUNT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_BACKLOG_THRESHOLD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_COUNT_CACHE_TTL;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRIORITIZE_DISABLED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRIORITIZE_THRESHOLD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TIME_PER_ITEM;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppUtils.class, SyncContext.class })
public class BaseReceiverSyncPrioritizingTaskTest {
	
	@Mock
	private SyncMessageRepository mockRepo;
	
	@Mock
	private Environment mockEnv;
	
	private MockReceiverSyncPrioritizingTask task;
	
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	
	@Before
	public void setup() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", true);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(SyncContext.class);
		task = Mockito.spy(new MockReceiverSyncPrioritizingTask());
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", 0);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countCacheTtl", 0);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap", (Object) null);
	}
	
	@Test
	public void initIfNecessary_shouldInitializeStaticFieldsIfSyncPrioritizeIsEnabled() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", 0);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countCacheTtl", 0);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap", (Object) null);
		final int syncThreshold = 100;
		final long countCacheTtl = 10000L;
		final long count = 150;
		when(mockEnv.getProperty(PROP_BACKLOG_THRESHOLD, Integer.class, DEFAULT_BACK_LOG)).thenReturn(1);
		when(mockEnv.getProperty(PROP_SYNC_TIME_PER_ITEM, int.class, DEFAULT_SYNC_TIME_PER_ITEM)).thenReturn(3);
		when(SyncContext.getBean(BEAN_NAME_SYNC_EXECUTOR)).thenReturn(executor);
		when(mockEnv.getProperty(PROP_PRIORITIZE_DISABLED, Boolean.class, false)).thenReturn(false);
		when(mockEnv.getProperty(eq(PROP_PRIORITIZE_THRESHOLD), eq(Integer.class), anyInt())).thenReturn(syncThreshold);
		when(mockEnv.getProperty(eq(PROP_COUNT_CACHE_TTL), eq(Long.class), anyLong())).thenReturn(countCacheTtl);
		Mockito.doAnswer(invocation -> count).when(task).getSyncCount();
		
		task.initIfNecessary();
		
		assertTrue(getInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized"));
		assertFalse(getInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled"));
		assertEquals(syncThreshold,
		    ((Integer) getInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold")).intValue());
		assertEquals(countCacheTtl,
		    ((Long) getInternalState(BaseReceiverSyncPrioritizingTask.class, "countCacheTtl")).longValue());
		assertEquals(count,
		    ((Map) getInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap")).get(KEY_SYNC_COUNT));
	}
	
	@Test
	public void initIfNecessary_shouldInitializeStaticFieldsToDefaultValues() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", 0);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countCacheTtl", 0);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap", (Object) null);
		final long count = 150;
		when(mockEnv.getProperty(PROP_BACKLOG_THRESHOLD, Integer.class, DEFAULT_BACK_LOG)).thenReturn(DEFAULT_BACK_LOG);
		when(mockEnv.getProperty(PROP_SYNC_TIME_PER_ITEM, int.class, DEFAULT_SYNC_TIME_PER_ITEM))
		        .thenReturn(DEFAULT_SYNC_TIME_PER_ITEM);
		when(SyncContext.getBean(BEAN_NAME_SYNC_EXECUTOR)).thenReturn(executor);
		when(mockEnv.getProperty(PROP_PRIORITIZE_DISABLED, Boolean.class, false)).thenReturn(false);
		int dailySyncSizePerThread = (DEFAULT_BACK_LOG * 86400000) / DEFAULT_SYNC_TIME_PER_ITEM;
		int dailySyncSize = dailySyncSizePerThread * executor.getMaximumPoolSize();
		when(mockEnv.getProperty(eq(PROP_PRIORITIZE_THRESHOLD), eq(Integer.class), eq(dailySyncSize)))
		        .thenReturn(dailySyncSize);
		when(mockEnv.getProperty(eq(PROP_COUNT_CACHE_TTL), eq(Long.class), eq(DEFAULT_SIZE_REFRESH_INTERVAL)))
		        .thenReturn(DEFAULT_SIZE_REFRESH_INTERVAL);
		Mockito.doAnswer(invocation -> count).when(task).getSyncCount();
		
		task.initIfNecessary();
		
		assertTrue(getInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized"));
		assertFalse(getInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled"));
		assertEquals(dailySyncSize,
		    ((Integer) getInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold")).intValue());
		assertEquals(DEFAULT_SIZE_REFRESH_INTERVAL,
		    ((Long) getInternalState(BaseReceiverSyncPrioritizingTask.class, "countCacheTtl")).longValue());
		assertEquals(count,
		    ((Map) getInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap")).get(KEY_SYNC_COUNT));
	}
	
	@Test
	public void initIfNecessary_shouldSkipIfAlreadyInitialized() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", true);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", 0);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countCacheTtl", 0);
		Map map = new PassiveExpiringMap();
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap", map);
		
		task.initIfNecessary();
		
		assertTrue(getInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized"));
		assertFalse(getInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled"));
		assertEquals(0, ((Integer) getInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold")).intValue());
		assertEquals(0, ((Long) getInternalState(BaseReceiverSyncPrioritizingTask.class, "countCacheTtl")).longValue());
		assertEquals(map, ((Map) getInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap")));
	}
	
	@Test
	public void run_shouldInvokeDoRun() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled", true);
		
		task.run();
		
		assertTrue(task.isDoRunCalled());
	}
	
	@Test
	public void run_shouldSkipRunningIfSyncPriorityIsEnabledAndSyncSizeThresholdIsNotExceeded() {
		Mockito.doAnswer(invocation -> true).when(task).isSyncSizeThresholdExceeded();
		
		task.run();
		
		assertFalse(task.isDoRunCalled());
	}
	
	@Test
	public void run_shouldInvokeDoRunIfSyncPriorityIsDisabledAndSyncSizeThresholdIsExceeded() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncPrioritizeDisabled", true);
		Mockito.doAnswer(invocation -> true).when(task).isSyncSizeThresholdExceeded();
		
		task.run();
		
		assertTrue(task.isDoRunCalled());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldReturnFalseIfTheThresholdIsNotExceeded() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", 3);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap", Collections.singletonMap(KEY_SYNC_COUNT, 2L));
		
		assertFalse(task.isSyncSizeThresholdExceeded());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldReturnFalseIfTheThresholdIsEqualed() {
		final Long count = 2L;
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", count.intValue());
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap",
		    Collections.singletonMap(KEY_SYNC_COUNT, count));
		
		assertFalse(task.isSyncSizeThresholdExceeded());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldReturnTrueIfTheThresholdIsExceeded() {
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", 2);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap", Collections.singletonMap(KEY_SYNC_COUNT, 3L));
		
		assertTrue(task.isSyncSizeThresholdExceeded());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldRefreshTheCountIfTheCachedValueIsExpired() {
		final Long newCount = 7L;
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "syncThreshold", 2);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap", new HashMap());
		Mockito.doAnswer(invocation -> newCount).when(task).getSyncCount();
		
		assertTrue(task.isSyncSizeThresholdExceeded());
		assertEquals(newCount,
		    ((Map) getInternalState(BaseReceiverSyncPrioritizingTask.class, "countMap")).get(KEY_SYNC_COUNT));
	}
	
	@Test
	public void getSyncCount_shouldFetchTheCount() {
		setInternalState(task, "syncRepo", mockRepo);
		
		task.getSyncCount();
		
		verify(mockRepo).count();
	}
	
}
