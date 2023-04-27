package org.openmrs.eip.app.receiver;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_IS_FILE;
import static org.openmrs.eip.app.receiver.SyncStatusProcessor.FLUSH_INTERVAL;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.repository.SiteSyncStatusRepository;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ReceiverContext.class, Utils.class })
public class SyncStatusProcessorTest {
	
	private static final String SITE_IDENTIFIER = "test";
	
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Mock
	private SiteSyncStatusRepository mockStatusRepo;
	
	@Mock
	private SiteInfo mockSite;
	
	private SyncStatusProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverContext.class);
		PowerMockito.mockStatic(Utils.class);
		when(ReceiverContext.getSiteInfo(SITE_IDENTIFIER)).thenReturn(mockSite);
		processor = new SyncStatusProcessor(mockStatusRepo);
	}
	
	@Before
	public void tearDown() {
		setInternalState(SyncStatusProcessor.class, "siteIdAndStatusMap", (Object) null);
	}
	
	@Test
	public void process_shouldSaveChangesForAnExistingSiteStatusIfItWasNotInTheCacheAndThenCacheIt() {
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(SITE_IDENTIFIER);
		SyncModel syncModel = new SyncModel();
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		exchange.getIn().setBody(syncModel);
		exchange.setProperty(EX_PROP_IS_FILE, false);
		ReceiverSyncStatus status = new ReceiverSyncStatus();
		status.setId(1L);
		when(mockStatusRepo.findBySiteInfo(mockSite)).thenReturn(status);
		
		processor.process(exchange);
		
		Mockito.verify(mockStatusRepo).save(status);
		Map<String, ReceiverSyncStatus> cache = getInternalState(SyncStatusProcessor.class, "siteIdAndStatusMap");
		Assert.assertEquals(status, cache.get(SITE_IDENTIFIER));
	}
	
	@Test
	public void process_shouldNotUpdateACachedSiteStatusIfDurationAfterLastSyncDateIsLessThanTheFlashInterval()
	    throws Exception {
		Date lastSyncDate = DATE_FORMAT.parse("2023-04-27 12:05:00");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(SITE_IDENTIFIER);
		SyncModel syncModel = new SyncModel();
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		
		exchange.getIn().setBody(syncModel);
		exchange.setProperty(EX_PROP_IS_FILE, false);
		ReceiverSyncStatus status = new ReceiverSyncStatus(mockSite, lastSyncDate);
		status.setId(1L);
		when(mockStatusRepo.findBySiteInfo(mockSite)).thenReturn(status);
		setInternalState(SyncStatusProcessor.class, "siteIdAndStatusMap", singletonMap(SITE_IDENTIFIER, status));
		when(Utils.getMillisElapsed(eq(lastSyncDate), any(Date.class))).thenReturn(FLUSH_INTERVAL - 1);
		
		processor.process(exchange);
		
		Mockito.verify(mockStatusRepo, never()).save(status);
		assertEquals(lastSyncDate, status.getLastSyncDate());
	}
	
	@Test
	public void process_shouldNotUpdateACachedSiteStatusIfDurationAfterLastSyncDateIsEqualToTheFlashInterval()
	    throws Exception {
		Date lastSyncDate = DATE_FORMAT.parse("2023-04-27 12:05:00");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(SITE_IDENTIFIER);
		SyncModel syncModel = new SyncModel();
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		
		exchange.getIn().setBody(syncModel);
		exchange.setProperty(EX_PROP_IS_FILE, false);
		ReceiverSyncStatus status = new ReceiverSyncStatus(mockSite, lastSyncDate);
		status.setId(1L);
		when(mockStatusRepo.findBySiteInfo(mockSite)).thenReturn(status);
		setInternalState(SyncStatusProcessor.class, "siteIdAndStatusMap", singletonMap(SITE_IDENTIFIER, status));
		when(Utils.getMillisElapsed(eq(lastSyncDate), any(Date.class))).thenReturn(FLUSH_INTERVAL);
		
		processor.process(exchange);
		
		Mockito.verify(mockStatusRepo, never()).save(status);
		assertEquals(lastSyncDate, status.getLastSyncDate());
	}
	
	@Test
	public void process_shouldSaveChangesForACachedSiteStatusIfDurationAfterLastSyncDateIsMoreThanTheFlashInterval()
	    throws Exception {
		Date lastSyncDate = DATE_FORMAT.parse("2023-04-27 12:05:00");
		SyncMetadata metadata = new SyncMetadata();
		metadata.setSourceIdentifier(SITE_IDENTIFIER);
		SyncModel syncModel = new SyncModel();
		syncModel.setMetadata(metadata);
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		
		exchange.getIn().setBody(syncModel);
		exchange.setProperty(EX_PROP_IS_FILE, false);
		ReceiverSyncStatus status = new ReceiverSyncStatus(mockSite, lastSyncDate);
		status.setId(1L);
		when(mockStatusRepo.findBySiteInfo(mockSite)).thenReturn(status);
		setInternalState(SyncStatusProcessor.class, "siteIdAndStatusMap", singletonMap(SITE_IDENTIFIER, status));
		when(Utils.getMillisElapsed(eq(lastSyncDate), any(Date.class))).thenReturn(FLUSH_INTERVAL + 1);
		Date timestamp = new Date();
		
		processor.process(exchange);
		
		Mockito.verify(mockStatusRepo).save(status);
		assertTrue(status.getLastSyncDate().getTime() >= timestamp.getTime());
	}
	
}
