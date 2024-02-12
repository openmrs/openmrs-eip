package org.openmrs.eip.app.receiver.reconcile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.time.ZoneId;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.ReconciliationRequest;
import org.openmrs.eip.app.management.entity.receiver.Reconciliation;
import org.openmrs.eip.app.management.entity.receiver.Reconciliation.ReconciliationStatus;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.entity.receiver.TableReconciliation;
import org.openmrs.eip.app.management.repository.ReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.TableReconciliationRepository;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.jms.core.JmsTemplate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ReceiverUtils.class)
public class ReconciliationProcessorTest {
	
	private static final int BATCH_SIZE = 100;
	
	@Mock
	private SiteRepository mockSiteRepo;
	
	@Mock
	private ReconciliationRepository mockRecRepo;
	
	@Mock
	private SiteReconciliationRepository mockSiteRecRepo;
	
	@Mock
	private TableReconciliationRepository mockTableRecRepo;
	
	@Mock
	private JmsTemplate mockJmsTemplate;
	
	private ReconciliationProcessor processor;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(ReceiverUtils.class);
		Whitebox.setInternalState(BaseQueueProcessor.class, "initialized", true);
		processor = new ReconciliationProcessor(null, mockSiteRepo, mockRecRepo, mockSiteRecRepo, mockTableRecRepo,
		        mockJmsTemplate);
		Whitebox.setInternalState(processor, "batchSize", BATCH_SIZE);
	}
	
	@After
	public void tearDown() {
		setInternalState(BaseQueueProcessor.class, "initialized", false);
	}
	
	@Test
	public void processItem_shouldStartTheReconciliation() {
		final String recIdentifier = "rec-id";
		final String siteIdentifier1 = "site-1";
		final String siteIdentifier2 = "site-2";
		final String siteQueue1 = "openmrs.sync." + siteIdentifier1;
		final String siteQueue2 = "openmrs.sync." + siteIdentifier2;
		SiteInfo site1 = new SiteInfo();
		site1.setIdentifier(siteIdentifier1);
		SiteInfo site2 = new SiteInfo();
		site2.setIdentifier(siteIdentifier2);
		SiteInfo site3 = new SiteInfo();
		site3.setIdentifier("site-3");
		Reconciliation rec = new Reconciliation();
		rec.setIdentifier(recIdentifier);
		assertEquals(ReconciliationStatus.NEW, rec.getStatus());
		when(mockSiteRepo.findAll()).thenReturn(List.of(site1, site3, site2));
		when(mockSiteRecRepo.getBySite(site3)).thenReturn(Mockito.mock(SiteReconciliation.class));
		when(ReceiverUtils.getSiteQueueName(siteIdentifier1)).thenReturn(siteQueue1);
		when(ReceiverUtils.getSiteQueueName(siteIdentifier2)).thenReturn(siteQueue2);
		long timestamp = System.currentTimeMillis();
		
		processor.processItem(rec);
		
		ReconciliationRequest request1 = new ReconciliationRequest();
		request1.setIdentifier(rec.getIdentifier());
		request1.setBatchSize(BATCH_SIZE);
		verify(mockJmsTemplate).convertAndSend(siteQueue1, JsonUtils.marshall(request1));
		ReconciliationRequest request2 = new ReconciliationRequest();
		request2.setIdentifier(rec.getIdentifier());
		request2.setBatchSize(BATCH_SIZE);
		verify(mockJmsTemplate).convertAndSend(siteQueue2, JsonUtils.marshall(request2));
		ArgumentCaptor<SiteReconciliation> siteRecArgCaptor = ArgumentCaptor.forClass(SiteReconciliation.class);
		verify(mockSiteRecRepo, times(2)).save(siteRecArgCaptor.capture());
		SiteReconciliation siteRec1 = siteRecArgCaptor.getAllValues().get(0);
		assertEquals(site1, siteRec1.getSite());
		assertTrue(siteRec1.getDateCreated().getTime() == timestamp || siteRec1.getDateCreated().getTime() > timestamp);
		SiteReconciliation siteRec2 = siteRecArgCaptor.getAllValues().get(1);
		assertEquals(site2, siteRec2.getSite());
		assertTrue(siteRec2.getDateCreated().getTime() == timestamp || siteRec2.getDateCreated().getTime() > timestamp);
		assertEquals(ReconciliationStatus.PROCESSING, rec.getStatus());
		verify(mockRecRepo).save(rec);
	}
	
	@Test
	public void processItem_shouldFinaliseReconciliationAfterAllSiteTablesHaveCompleted() {
		SiteInfo mockSite1 = Mockito.mock(SiteInfo.class);
		SiteInfo mockSite2 = Mockito.mock(SiteInfo.class);
		SiteReconciliation siteRec1 = new SiteReconciliation();
		SiteReconciliation siteRec2 = new SiteReconciliation();
		assertNull(siteRec1.getDateCompleted());
		assertNull(siteRec2.getDateCompleted());
		Reconciliation rec = new Reconciliation();
		rec.setStatus(ReconciliationStatus.PROCESSING);
		when(mockSiteRepo.findAll()).thenReturn(List.of(mockSite1, mockSite2));
		when(mockSiteRecRepo.getBySite(mockSite1)).thenReturn(siteRec1);
		when(mockSiteRecRepo.getBySite(mockSite2)).thenReturn(siteRec2);
		TableReconciliation mockTableRec = Mockito.mock(TableReconciliation.class);
		when(mockTableRec.isCompleted()).thenReturn(true);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(eq(siteRec1), anyString())).thenReturn(mockTableRec);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(eq(siteRec2), anyString())).thenReturn(mockTableRec);
		long timestamp = System.currentTimeMillis();
		
		processor.processItem(rec);
		
		verify(mockSiteRecRepo).save(siteRec1);
		verify(mockSiteRecRepo).save(siteRec2);
		verify(mockRecRepo).save(rec);
		long dateCompletedMillis = siteRec1.getDateCompleted().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		assertTrue(dateCompletedMillis == timestamp || dateCompletedMillis > timestamp);
		dateCompletedMillis = siteRec2.getDateCompleted().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		assertTrue(dateCompletedMillis == timestamp || dateCompletedMillis > timestamp);
		assertEquals(ReconciliationStatus.FINALIZING, rec.getStatus());
	}
	
	@Test
	public void processItem_shouldNotFinaliseReconciliationIfThereAreIncompleteTables() {
		SiteInfo mockSite1 = Mockito.mock(SiteInfo.class);
		SiteInfo mockSite2 = Mockito.mock(SiteInfo.class);
		SiteReconciliation siteRec1 = new SiteReconciliation();
		SiteReconciliation siteRec2 = new SiteReconciliation();
		assertNull(siteRec1.getDateCompleted());
		assertNull(siteRec2.getDateCompleted());
		Reconciliation rec = new Reconciliation();
		rec.setStatus(ReconciliationStatus.PROCESSING);
		when(mockSiteRepo.findAll()).thenReturn(List.of(mockSite1, mockSite2));
		when(mockSiteRecRepo.getBySite(mockSite1)).thenReturn(siteRec1);
		when(mockSiteRecRepo.getBySite(mockSite2)).thenReturn(siteRec2);
		TableReconciliation mockTableRec1 = Mockito.mock(TableReconciliation.class);
		when(mockTableRec1.isCompleted()).thenReturn(true);
		TableReconciliation mockTableRec2 = Mockito.mock(TableReconciliation.class);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(eq(siteRec1), anyString())).thenReturn(mockTableRec1);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(eq(siteRec2), anyString())).thenReturn(mockTableRec2);
		long timestamp = System.currentTimeMillis();
		
		processor.processItem(rec);
		
		verify(mockSiteRecRepo).save(siteRec1);
		verify(mockSiteRecRepo, never()).save(siteRec2);
		verify(mockRecRepo, never()).save(rec);
		long dateCompletedMillis = siteRec1.getDateCompleted().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		assertTrue(dateCompletedMillis == timestamp || dateCompletedMillis > timestamp);
		assertNull(siteRec2.getDateCompleted());
		assertEquals(ReconciliationStatus.PROCESSING, rec.getStatus());
	}
	
	@Test
	public void processItem_shouldNotFinaliseReconciliationIfThereAreMissingTableReconciliations() {
		SiteInfo mockSite1 = Mockito.mock(SiteInfo.class);
		SiteInfo mockSite2 = Mockito.mock(SiteInfo.class);
		SiteReconciliation siteRec1 = new SiteReconciliation();
		SiteReconciliation siteRec2 = new SiteReconciliation();
		assertNull(siteRec1.getDateCompleted());
		assertNull(siteRec2.getDateCompleted());
		Reconciliation rec = new Reconciliation();
		rec.setStatus(ReconciliationStatus.PROCESSING);
		when(mockSiteRepo.findAll()).thenReturn(List.of(mockSite1, mockSite2));
		when(mockSiteRecRepo.getBySite(mockSite1)).thenReturn(siteRec1);
		when(mockSiteRecRepo.getBySite(mockSite2)).thenReturn(siteRec2);
		TableReconciliation mockTableRec1 = Mockito.mock(TableReconciliation.class);
		when(mockTableRec1.isCompleted()).thenReturn(true);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(eq(siteRec1), anyString())).thenReturn(mockTableRec1);
		long timestamp = System.currentTimeMillis();
		
		processor.processItem(rec);
		
		verify(mockSiteRecRepo).save(siteRec1);
		verify(mockSiteRecRepo, never()).save(siteRec2);
		verify(mockRecRepo, never()).save(rec);
		long dateCompletedMillis = siteRec1.getDateCompleted().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		assertTrue(dateCompletedMillis == timestamp || dateCompletedMillis > timestamp);
		assertNull(siteRec2.getDateCompleted());
		assertEquals(ReconciliationStatus.PROCESSING, rec.getStatus());
	}
	
}
