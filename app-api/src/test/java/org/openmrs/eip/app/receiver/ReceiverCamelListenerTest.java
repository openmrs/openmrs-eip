package org.openmrs.eip.app.receiver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.camel.spi.CamelEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
@PrepareForTest({ SyncContext.class, ReceiverContext.class })
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
	private ScheduledThreadPoolExecutor mockExecutor;
	
	@Mock
	private ReceiverActiveMqMessagePublisher mockPublisher;
	
	private ReceiverCamelListener listener;
	
	private long testInitialDelay = 2;
	
	private long testDelay = 3;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(ReceiverContext.class);
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
		User testAppUser = new User();
		testAppUser.setId(TEST_OPENMRS_USER_ID);
		when(mockEnv.getProperty(Constants.PROP_OPENMRS_USER)).thenReturn(TEST_OPENMRS_USERNAME);
		when(SyncContext.getBean(UserRepository.class)).thenReturn(mockUserRepo);
		when(mockUserRepo.findOne(any(Example.class))).thenReturn(Optional.of(testAppUser));
		when(SyncContext.getBean(UserLightRepository.class)).thenReturn(mockUserLightRepo);
		when(mockUserLightRepo.findById(TEST_OPENMRS_USER_ID)).thenReturn(Optional.of(new UserLight()));
		listener = new ReceiverCamelListener(mockExecutor, null);
		setInternalState(SiteMessageConsumer.class, "initialized", true);
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		setInternalState(listener, "initialDelayConsumer", testInitialDelay);
		setInternalState(listener, "delayConsumer", testDelay);
		setInternalState(listener, "initialDelayCacheEvictor", testInitialDelay);
		setInternalState(listener, "delayCacheEvictor", testDelay);
		setInternalState(listener, "initialDelayIndexUpdater", testInitialDelay);
		setInternalState(listener, "delayIndexUpdater", testDelay);
		setInternalState(listener, "initialDelayResponseSender", testInitialDelay);
		setInternalState(listener, "delayResponseSender", testDelay);
		setInternalState(listener, "initialDelayArchiver", testInitialDelay);
		setInternalState(listener, "delayArchiver", testDelay);
		when(SyncContext.getBean(ReceiverActiveMqMessagePublisher.class)).thenReturn(mockPublisher);
	}
	
	@Test
	public void notify_shouldOnlyStartSiteConsumersForSitesThatAreNotDisabled() {
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
		
		Mockito.verify(mockExecutor).scheduleWithFixedDelay(any(SiteMessageConsumer.class), eq(testInitialDelay),
		    eq(testDelay), eq(TimeUnit.MILLISECONDS));
		
		Mockito.verify(mockExecutor).scheduleWithFixedDelay(any(CacheEvictor.class), eq(testInitialDelay), eq(testDelay),
		    eq(TimeUnit.MILLISECONDS));
		
		Mockito.verify(mockExecutor).scheduleWithFixedDelay(any(SearchIndexUpdater.class), eq(testInitialDelay),
		    eq(testDelay), eq(TimeUnit.MILLISECONDS));
		
		Mockito.verify(mockExecutor).scheduleWithFixedDelay(any(SyncResponseSender.class), eq(testInitialDelay),
		    eq(testDelay), eq(TimeUnit.MILLISECONDS));
		
		Mockito.verify(mockExecutor).scheduleWithFixedDelay(any(SyncedMessageArchiver.class), eq(testInitialDelay),
		    eq(testDelay), eq(TimeUnit.MILLISECONDS));
	}
	
}
