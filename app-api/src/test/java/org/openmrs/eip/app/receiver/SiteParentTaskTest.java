package org.openmrs.eip.app.receiver;

import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.SyncContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class SiteParentTaskTest {
	
	private static final String SITE_IDENTIFIER = "test-site-id";
	
	private static final String QUEUE_NAME = "test-queue";
	
	@Mock
	private SiteInfo mockSite;
	
	@Mock
	private SiteMessageConsumer mockSynchronizer;
	
	@Mock
	private CacheEvictor mockEvictor;
	
	@Mock
	private SearchIndexUpdater mockUpdater;
	
	@Mock
	private SyncResponseSender mockResponseSender;
	
	@Mock
	private SyncedMessageArchiver mockArchiver;
	
	@Mock
	private SyncedMessageDeleter mockDeleter;
	
	@Mock
	private ReceiverActiveMqMessagePublisher mockPublisher;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		setInternalState(SiteMessageConsumer.class, "initialized", true);
		setInternalState(BaseSiteRunnable.class, "initialized", true);
		when(SyncContext.getBean(ReceiverActiveMqMessagePublisher.class)).thenReturn(mockPublisher);
		when(mockSite.getIdentifier()).thenReturn(SITE_IDENTIFIER);
		when(mockPublisher.getCamelOutputEndpoint(SITE_IDENTIFIER)).thenReturn("activemq:" + QUEUE_NAME);
	}
	
	@After
	public void tearDown() {
		setInternalState(SiteMessageConsumer.class, "initialized", false);
		setInternalState(BaseSiteRunnable.class, "initialized", false);
	}
	
	@Test
	public void shouldOnlyCreateInstancesOfTasksThatAreANotDisabled() {
		List<Class<? extends Runnable>> disabledClasses = Arrays.asList(SyncedMessageArchiver.class,
		    SyncedMessageDeleter.class);
		
		SiteParentTask task = new SiteParentTask(mockSite, null, disabledClasses);
		
		Assert.assertNotNull(Whitebox.getInternalState(task, "synchronizer"));
		Assert.assertNotNull(Whitebox.getInternalState(task, "evictor"));
		Assert.assertNotNull(Whitebox.getInternalState(task, "updater"));
		Assert.assertNotNull(Whitebox.getInternalState(task, "responseSender"));
		Assert.assertNull(Whitebox.getInternalState(task, "archiver"));
		Assert.assertNull(Whitebox.getInternalState(task, "deleter"));
	}
	
	@Test
	public void doRun_shouldRunTheChildTasks() throws Exception {
		SiteParentTask task = new SiteParentTask(mockSite, null, Collections.emptyList());
		setInternalState(task, "synchronizer", mockSynchronizer);
		setInternalState(task, "evictor", mockEvictor);
		setInternalState(task, "updater", mockUpdater);
		setInternalState(task, "responseSender", mockResponseSender);
		setInternalState(task, "archiver", mockArchiver);
		setInternalState(task, "deleter", mockDeleter);
		
		task.doRun();
		
		Mockito.verify(mockSynchronizer).run();
		Mockito.verify(mockEvictor).run();
		Mockito.verify(mockUpdater).run();
		Mockito.verify(mockResponseSender).run();
		Mockito.verify(mockArchiver).run();
		Mockito.verify(mockDeleter).run();
	}
	
	@Test
	public void doRun_shouldSkipRunningTheDisabledChildTasks() throws Exception {
		List<Class<? extends Runnable>> disabledClasses = Arrays.asList(SyncedMessageArchiver.class,
		    SyncedMessageDeleter.class);
		SiteParentTask task = new SiteParentTask(mockSite, null, disabledClasses);
		setInternalState(task, "synchronizer", mockSynchronizer);
		setInternalState(task, "evictor", mockEvictor);
		setInternalState(task, "updater", mockUpdater);
		setInternalState(task, "responseSender", mockResponseSender);
		
		task.doRun();
		
		Mockito.verify(mockSynchronizer).run();
		Mockito.verify(mockEvictor).run();
		Mockito.verify(mockUpdater).run();
		Mockito.verify(mockResponseSender).run();
		Mockito.verifyNoInteractions(mockArchiver);
		Mockito.verifyNoInteractions(mockDeleter);
	}
	
}
