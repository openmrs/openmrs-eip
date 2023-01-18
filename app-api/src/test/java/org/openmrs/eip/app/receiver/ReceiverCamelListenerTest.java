package org.openmrs.eip.app.receiver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
import org.powermock.reflect.Whitebox;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Executors.class, SyncContext.class, ReceiverContext.class })
public class ReceiverCamelListenerTest {
	
	private static final String TEST_OPENMRS_USERNAME = "openmrs_user";
	
	private static final Long TEST_OPENMRS_USER_ID = 101L;
	
	private static final int TEST_THREAD_COUNT = 1;
	
	@Mock
	private Environment mockEnv;
	
	@Mock
	private UserRepository mockUserRepo;
	
	@Mock
	private UserLightRepository mockUserLightRepo;
	
	@Mock
	private ScheduledExecutorService mockExecutor;
	
	private ReceiverCamelListener listener;
	
	private long mockDelay = 3;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(Executors.class);
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(ReceiverContext.class);
		Mockito.when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
		User testAppUser = new User();
		testAppUser.setId(TEST_OPENMRS_USER_ID);
		Mockito.when(mockEnv.getProperty(Constants.PROP_OPENMRS_USER)).thenReturn(TEST_OPENMRS_USERNAME);
		Mockito.when(SyncContext.getBean(UserRepository.class)).thenReturn(mockUserRepo);
		Mockito.when(mockUserRepo.findOne(any(Example.class))).thenReturn(Optional.of(testAppUser));
		Mockito.when(SyncContext.getBean(UserLightRepository.class)).thenReturn(mockUserLightRepo);
		Mockito.when(mockUserLightRepo.findById(TEST_OPENMRS_USER_ID)).thenReturn(Optional.of(new UserLight()));
		listener = new ReceiverCamelListener();
		Whitebox.setInternalState(listener, "parallelSiteSize", TEST_THREAD_COUNT);
		Whitebox.setInternalState(listener, "threads", TEST_THREAD_COUNT);
		Whitebox.setInternalState(listener, "delay", mockDelay);
	}
	
	@Test
	public void notify_shouldOnlyStartSiteConsumersForSitesThatAreNotDisabled() {
		SiteInfo siteInfo1 = new SiteInfo();
		siteInfo1.setName("site1");
		siteInfo1.setDisabled(true);
		SiteInfo siteInfo2 = new SiteInfo();
		siteInfo2.setName("site2");
		siteInfo2.setDisabled(false);
		Collection<SiteInfo> sites = Stream.of(siteInfo1, siteInfo2).collect(Collectors.toList());
		Mockito.when(ReceiverContext.getSites()).thenAnswer(invocation -> {
			Whitebox.setInternalState(listener, "siteExecutor", mockExecutor);
			return sites;
		});
		
		listener.notify((CamelEvent.CamelContextStartedEvent) () -> null);
		
		Mockito.verify(mockExecutor).scheduleWithFixedDelay(any(SiteMessageConsumer.class), eq(2l), eq(mockDelay),
		    eq(TimeUnit.SECONDS));
	}
	
}
