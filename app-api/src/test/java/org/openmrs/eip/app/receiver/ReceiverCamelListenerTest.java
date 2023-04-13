package org.openmrs.eip.app.receiver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.camel.spi.CamelEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.User;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.UserRepository;
import org.openmrs.eip.component.repository.light.UserLightRepository;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SyncContext.class, ReceiverContext.class, AppUtils.class })
public class ReceiverCamelListenerTest {
	
	private static final String TEST_OPENMRS_USERNAME = "openmrs_user";
	
	private static final Long TEST_OPENMRS_USER_ID = 101L;
	
	@Mock
	private Environment mockEnv;
	
	@Mock
	private UserRepository mockUserRepo;
	
	@Mock
	private UserLightRepository mockUserLightRepo;
	
	@Mock
	private ScheduledThreadPoolExecutor mockSiteExecutor;
	
	@Mock
	private ThreadPoolExecutor mockSyncExecutor;
	
	@Mock
	private ReceiverActiveMqMessagePublisher mockPublisher;
	
	private ReceiverCamelListener listener;
	
	private long testInitialDelay = 2;
	
	private long testDelay = 3;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(ReceiverContext.class);
		PowerMockito.mockStatic(AppUtils.class);
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
		User testAppUser = new User();
		testAppUser.setId(TEST_OPENMRS_USER_ID);
		when(mockEnv.getProperty(Constants.PROP_OPENMRS_USER)).thenReturn(TEST_OPENMRS_USERNAME);
		when(SyncContext.getBean(UserRepository.class)).thenReturn(mockUserRepo);
		when(mockUserRepo.findOne(any(Example.class))).thenReturn(Optional.of(testAppUser));
		when(SyncContext.getBean(UserLightRepository.class)).thenReturn(mockUserLightRepo);
		when(mockUserLightRepo.findById(TEST_OPENMRS_USER_ID)).thenReturn(Optional.of(new UserLight()));
		listener = new ReceiverCamelListener(mockSiteExecutor, mockSyncExecutor);
		setInternalState(SiteMessageConsumer.class, "initialized", true);
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", true);
		setInternalState(listener, "siteTaskInitialDelay", testInitialDelay);
		setInternalState(listener, "siteTaskDelay", testDelay);
		setInternalState(listener, "disabledTaskTypes", Collections.emptyList());
		setInternalState(listener, "initialDelayPruner", testInitialDelay);
		setInternalState(listener, "delayPruner", testDelay);
		when(SyncContext.getBean(ReceiverActiveMqMessagePublisher.class)).thenReturn(mockPublisher);
	}
	
	@After
	public void tearDown() {
		setInternalState(SiteMessageConsumer.class, "initialized", false);
		setInternalState(BaseSiteRunnable.class, "initialized", false);
		setInternalState(BaseReceiverSyncPrioritizingTask.class, "initialized", false);
	}
	
	@Test
	public void notify_shouldOnlyStartSiteTasksForSitesThatAreNotDisabled() {
		SiteInfo siteInfo1 = new SiteInfo();
		siteInfo1.setIdentifier("site1");
		siteInfo1.setDisabled(true);
		SiteInfo siteInfo2 = new SiteInfo();
		final String siteIdentifier = "site2";
		siteInfo2.setIdentifier(siteIdentifier);
		siteInfo2.setDisabled(false);
		when(mockPublisher.getCamelOutputEndpoint(siteIdentifier)).thenReturn("activemq:test");
		Collection<SiteInfo> sites = Stream.of(siteInfo1, siteInfo2).collect(Collectors.toList());
		when(ReceiverContext.getSites()).thenReturn(sites);
		
		listener.notify((CamelEvent.CamelContextStartedEvent) () -> null);
		
		Mockito.verify(mockSiteExecutor).scheduleWithFixedDelay(any(SiteParentTask.class), eq(testInitialDelay),
		    eq(testDelay), eq(TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void notify_shouldOnlyStartSiteConsumersForSitesThatAreNotDisabled() {
		final String siteIdentifier = "site2";
		SiteInfo site = new SiteInfo();
		site.setIdentifier(siteIdentifier);
		site.setDisabled(false);
		when(mockPublisher.getCamelOutputEndpoint(siteIdentifier)).thenReturn("activemq:test");
		Collection<SiteInfo> sites = Collections.singletonList(site);
		when(ReceiverContext.getSites()).thenReturn(sites);
		setInternalState(listener, "prunerEnabled", true);
		setInternalState(listener, "archivesMaxAgeInDays", 1);
		
		listener.notify((CamelEvent.CamelContextStartedEvent) () -> null);
		
		Mockito.verify(mockSiteExecutor).scheduleWithFixedDelay(any(SiteParentTask.class), eq(testInitialDelay),
		    eq(testDelay), eq(TimeUnit.MILLISECONDS));
		
		Mockito.verify(mockSiteExecutor).scheduleWithFixedDelay(any(ReceiverArchivePruningTask.class), eq(testInitialDelay),
		    eq(testDelay), eq(TimeUnit.MILLISECONDS));
	}
	
	@Test
	public void notify_shouldCleanUpWhenApplicationContextIsStopped() {
		when(mockSyncExecutor.isTerminated()).thenReturn(true);
		final String siteName1 = "task 1";
		final String siteName2 = "task 2";
		SiteParentTask mockTask1 = Mockito.mock(SiteParentTask.class);
		SiteInfo site1 = new SiteInfo();
		site1.setName(siteName1);
		when(mockTask1.getSiteInfo()).thenReturn(site1);
		when(mockTask1.getChildExecutor()).thenReturn(mockSyncExecutor);
		SiteParentTask mockTask2 = Mockito.mock(SiteParentTask.class);
		SiteInfo site2 = new SiteInfo();
		site2.setName(siteName2);
		when(mockTask2.getSiteInfo()).thenReturn(site2);
		when(mockTask2.getChildExecutor()).thenReturn(mockSyncExecutor);
		setInternalState(ReceiverCamelListener.class, "siteTasks", Arrays.asList(mockTask1, mockTask2));
		
		listener.notify((CamelEvent.CamelContextStoppingEvent) () -> null);
		
		PowerMockito.verifyStatic(AppUtils.class);
		AppUtils.shutdownExecutor(mockSyncExecutor, siteName1 + " " + ReceiverConstants.CHILD_TASK_NAME, true);
		PowerMockito.verifyStatic(AppUtils.class);
		AppUtils.shutdownExecutor(mockSyncExecutor, siteName2 + " " + ReceiverConstants.CHILD_TASK_NAME, true);
		PowerMockito.verifyStatic(AppUtils.class);
		AppUtils.shutdownExecutor(mockSiteExecutor, ReceiverConstants.PARENT_TASK_NAME, false);
	}
	
}
