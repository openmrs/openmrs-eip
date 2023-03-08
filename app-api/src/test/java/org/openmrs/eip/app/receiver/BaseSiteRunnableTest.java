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
import static org.openmrs.eip.app.receiver.BaseSiteRunnable.DEFAULT_BACK_LOG;
import static org.openmrs.eip.app.receiver.BaseSiteRunnable.DEFAULT_SIZE_REFRESH_INTERVAL;
import static org.openmrs.eip.app.receiver.BaseSiteRunnable.DEFAULT_SYNC_TIME_PER_ITEM;
import static org.openmrs.eip.app.receiver.BaseSiteRunnable.KEY_SYNC_COUNT;
import static org.openmrs.eip.app.receiver.ReceiverConstants.DEFAULT_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_BACKLOG_THRESHOLD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_COUNT_CACHE_TTL;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRIORITIZE_DISABLED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRIORITIZE_THRESHOLD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TASK_BATCH_SIZE;
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
import org.openmrs.eip.MockBaseSiteRunnable;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppUtils.class, SyncContext.class })
public class BaseSiteRunnableTest {
	
	@Mock
	private SiteInfo mockSite;
	
	@Mock
	private SyncMessageRepository mockRepo;
	
	private MockBaseSiteRunnable runnable;
	
	@Mock
	private Environment mockEnv;
	
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
	
	@Before
	public void setup() {
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(SyncContext.class);
		runnable = Mockito.spy(new MockBaseSiteRunnable(mockSite));
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
		setInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled", false);
		setInternalState(BaseSiteRunnable.class, "syncThreshold", 0);
		setInternalState(BaseSiteRunnable.class, "countCacheTtl", 0);
		setInternalState(BaseSiteRunnable.class, "page", (Object) null);
		setInternalState(BaseSiteRunnable.class, "countMap", (Object) null);
	}
	
	@Test
	public void initIfNecessary_shouldInitializeStaticFieldsIfSyncPrioritizeIsEnabled() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
		setInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled", false);
		setInternalState(BaseSiteRunnable.class, "page", (Object) null);
		setInternalState(BaseSiteRunnable.class, "syncThreshold", 0);
		setInternalState(BaseSiteRunnable.class, "countCacheTtl", 0);
		setInternalState(BaseSiteRunnable.class, "countMap", (Object) null);
		final int batchSize = 4;
		final int syncThreshold = 100;
		final long countCacheTtl = 10000L;
		final long count = 150;
		when(mockEnv.getProperty(PROP_BACKLOG_THRESHOLD, Integer.class, DEFAULT_BACK_LOG)).thenReturn(1);
		when(mockEnv.getProperty(PROP_SYNC_TIME_PER_ITEM, int.class, DEFAULT_SYNC_TIME_PER_ITEM)).thenReturn(3);
		when(SyncContext.getBean(BEAN_NAME_SYNC_EXECUTOR)).thenReturn(executor);
		when(mockEnv.getProperty(PROP_PRIORITIZE_DISABLED, Boolean.class, false)).thenReturn(false);
		when(mockEnv.getProperty(eq(PROP_SYNC_TASK_BATCH_SIZE), eq(Integer.class), anyInt())).thenReturn(batchSize);
		when(mockEnv.getProperty(eq(PROP_PRIORITIZE_THRESHOLD), eq(Integer.class), anyInt())).thenReturn(syncThreshold);
		when(mockEnv.getProperty(eq(PROP_COUNT_CACHE_TTL), eq(Long.class), anyLong())).thenReturn(countCacheTtl);
		Mockito.doAnswer(invocation -> count).when(runnable).getSyncCount();
		
		runnable.initIfNecessary();
		
		assertTrue(getInternalState(BaseSiteRunnable.class, "initialized"));
		assertFalse(getInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled"));
		assertEquals(batchSize, ((Pageable) getInternalState(BaseSiteRunnable.class, "page")).getPageSize());
		assertEquals(syncThreshold, ((Integer) getInternalState(BaseSiteRunnable.class, "syncThreshold")).intValue());
		assertEquals(countCacheTtl, ((Long) getInternalState(BaseSiteRunnable.class, "countCacheTtl")).longValue());
		assertEquals(count, ((Map) getInternalState(BaseSiteRunnable.class, "countMap")).get(KEY_SYNC_COUNT));
	}
	
	@Test
	public void initIfNecessary_shouldInitializeStaticFieldsToDefaultValues() {
		setInternalState(BaseSiteRunnable.class, "initialized", false);
		setInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled", false);
		setInternalState(BaseSiteRunnable.class, "page", (Object) null);
		setInternalState(BaseSiteRunnable.class, "syncThreshold", 0);
		setInternalState(BaseSiteRunnable.class, "countCacheTtl", 0);
		setInternalState(BaseSiteRunnable.class, "countMap", (Object) null);
		final long count = 150;
		when(mockEnv.getProperty(PROP_BACKLOG_THRESHOLD, Integer.class, DEFAULT_BACK_LOG)).thenReturn(DEFAULT_BACK_LOG);
		when(mockEnv.getProperty(PROP_SYNC_TIME_PER_ITEM, int.class, DEFAULT_SYNC_TIME_PER_ITEM))
		        .thenReturn(DEFAULT_SYNC_TIME_PER_ITEM);
		when(SyncContext.getBean(BEAN_NAME_SYNC_EXECUTOR)).thenReturn(executor);
		when(mockEnv.getProperty(PROP_PRIORITIZE_DISABLED, Boolean.class, false)).thenReturn(false);
		when(mockEnv.getProperty(eq(PROP_SYNC_TASK_BATCH_SIZE), eq(Integer.class), eq(DEFAULT_TASK_BATCH_SIZE)))
		        .thenReturn(DEFAULT_TASK_BATCH_SIZE);
		int dailySyncSizePerThread = (DEFAULT_BACK_LOG * 86400000) / DEFAULT_SYNC_TIME_PER_ITEM;
		int dailySyncSize = dailySyncSizePerThread * executor.getMaximumPoolSize();
		when(mockEnv.getProperty(eq(PROP_PRIORITIZE_THRESHOLD), eq(Integer.class), eq(dailySyncSize)))
		        .thenReturn(dailySyncSize);
		when(mockEnv.getProperty(eq(PROP_COUNT_CACHE_TTL), eq(Long.class), eq(DEFAULT_SIZE_REFRESH_INTERVAL)))
		        .thenReturn(DEFAULT_SIZE_REFRESH_INTERVAL);
		Mockito.doAnswer(invocation -> count).when(runnable).getSyncCount();
		
		runnable.initIfNecessary();
		
		assertTrue(getInternalState(BaseSiteRunnable.class, "initialized"));
		assertFalse(getInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled"));
		assertEquals(DEFAULT_TASK_BATCH_SIZE, ((Pageable) getInternalState(BaseSiteRunnable.class, "page")).getPageSize());
		assertEquals(dailySyncSize, ((Integer) getInternalState(BaseSiteRunnable.class, "syncThreshold")).intValue());
		assertEquals(DEFAULT_SIZE_REFRESH_INTERVAL,
		    ((Long) getInternalState(BaseSiteRunnable.class, "countCacheTtl")).longValue());
		assertEquals(count, ((Map) getInternalState(BaseSiteRunnable.class, "countMap")).get(KEY_SYNC_COUNT));
	}
	
	@Test
	public void initIfNecessary_shouldSkipIfAlreadyInitialized() {
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		setInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled", false);
		final int batchSize = 4;
		Pageable page = PageRequest.of(0, batchSize);
		setInternalState(BaseSiteRunnable.class, "page", page);
		setInternalState(BaseSiteRunnable.class, "syncThreshold", 0);
		setInternalState(BaseSiteRunnable.class, "countCacheTtl", 0);
		Map map = new PassiveExpiringMap();
		setInternalState(BaseSiteRunnable.class, "countMap", map);
		
		runnable.initIfNecessary();
		
		assertTrue(getInternalState(BaseSiteRunnable.class, "initialized"));
		assertFalse(getInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled"));
		assertEquals(batchSize, ((Pageable) getInternalState(BaseSiteRunnable.class, "page")).getPageSize());
		assertEquals(0, ((Integer) getInternalState(BaseSiteRunnable.class, "syncThreshold")).intValue());
		assertEquals(0, ((Long) getInternalState(BaseSiteRunnable.class, "countCacheTtl")).longValue());
		assertEquals(map, ((Map) getInternalState(BaseSiteRunnable.class, "countMap")));
	}
	
	@Test
	public void run_shouldInvokeDoRun() {
		setInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled", true);
		runnable.run();
		
		assertTrue(runnable.isDoRunCalled());
	}
	
	@Test
	public void run_shouldSkipRunningIfTheApplicationIsStopping() {
		when(AppUtils.isStopping()).thenReturn(true);
		
		runnable.run();
		
		assertFalse(runnable.isDoRunCalled());
	}
	
	@Test
	public void run_shouldSkipRunningIfSyncPriorityIsEnabledAndSyncSizeThresholdIsNotExceeded() {
		Mockito.doAnswer(invocation -> true).when(runnable).isSyncSizeThresholdExceeded();
		
		runnable.run();
		
		assertFalse(runnable.isDoRunCalled());
	}
	
	@Test
	public void run_shouldInvokeDoRunIfSyncPriorityIsDisabledAndSyncSizeThresholdIsExceeded() {
		setInternalState(BaseSiteRunnable.class, "syncPrioritizeDisabled", true);
		Mockito.doAnswer(invocation -> true).when(runnable).isSyncSizeThresholdExceeded();
		
		runnable.run();
		
		assertTrue(runnable.isDoRunCalled());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldReturnFalseIfTheThresholdIsNotExceeded() {
		setInternalState(BaseSiteRunnable.class, "syncThreshold", 3);
		setInternalState(BaseSiteRunnable.class, "countMap", Collections.singletonMap(KEY_SYNC_COUNT, 2L));
		
		assertFalse(runnable.isSyncSizeThresholdExceeded());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldReturnFalseIfTheThresholdIsEqualed() {
		final Long count = 2L;
		setInternalState(BaseSiteRunnable.class, "syncThreshold", count.intValue());
		setInternalState(BaseSiteRunnable.class, "countMap", Collections.singletonMap(KEY_SYNC_COUNT, count));
		
		assertFalse(runnable.isSyncSizeThresholdExceeded());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldReturnTrueIfTheThresholdIsExceeded() {
		setInternalState(BaseSiteRunnable.class, "syncThreshold", 2);
		setInternalState(BaseSiteRunnable.class, "countMap", Collections.singletonMap(KEY_SYNC_COUNT, 3L));
		
		assertTrue(runnable.isSyncSizeThresholdExceeded());
	}
	
	@Test
	public void isSyncSizeThresholdExceeded_shouldRefreshTheCountIfTheCachedValueIsExpired() {
		final Long newCount = 7L;
		setInternalState(BaseSiteRunnable.class, "syncThreshold", 2);
		setInternalState(BaseSiteRunnable.class, "countMap", new HashMap());
		Mockito.doAnswer(invocation -> newCount).when(runnable).getSyncCount();
		
		assertTrue(runnable.isSyncSizeThresholdExceeded());
		assertEquals(newCount, ((Map) getInternalState(BaseSiteRunnable.class, "countMap")).get(KEY_SYNC_COUNT));
	}
	
	@Test
	public void getSyncCount_shouldFetchTheCountFromThe() {
		setInternalState(runnable, "syncRepo", mockRepo);
		
		runnable.getSyncCount();
		
		verify(mockRepo).count();
	}
	
}
