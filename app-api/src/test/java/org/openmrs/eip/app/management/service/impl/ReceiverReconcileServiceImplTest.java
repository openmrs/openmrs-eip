package org.openmrs.eip.app.management.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.management.service.impl.ReceiverReconcileServiceImpl.OPERATIONS;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.MissingEntity;
import org.openmrs.eip.app.management.entity.receiver.ReceiverTableReconciliation;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.app.management.repository.MissingEntityRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncRequestRepository;
import org.openmrs.eip.app.management.repository.ReceiverTableReconcileRepository;
import org.openmrs.eip.app.management.repository.ReconciliationMsgRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ReceiverReconcileServiceImplTest {
	
	@Mock
	private JmsMessageRepository mockJmsMsgRepo;
	
	@Mock
	private SyncMessageRepository mockSyncMsgRepo;
	
	@Mock
	private ReceiverRetryRepository mockRetryRepo;
	
	@Mock
	private ReceiverSyncRequestRepository mockRequestRepo;
	
	@Mock
	private ReconciliationMsgRepository mockRecMsgRep;
	
	@Mock
	private SiteReconciliationRepository mockSiteRecRepo;
	
	@Mock
	private ReceiverTableReconcileRepository mockTableRecRepo;
	
	@Mock
	private MissingEntityRepository mockMissingRepo;
	
	@Mock
	private SiteRepository mockSiteRepo;
	
	@Mock
	private SiteInfo mockSite;
	
	private ReceiverReconcileServiceImpl service;
	
	@Before
	public void setup() {
		service = new ReceiverReconcileServiceImpl(mockSiteRepo, mockRecMsgRep, mockJmsMsgRepo, mockRequestRepo,
		        mockSiteRecRepo, mockTableRecRepo, mockMissingRepo, mockSyncMsgRepo, mockRetryRepo);
	}
	
	@Test
	public void updateReconciliationMessage_shouldCheckForEntityExistenceInTheSyncAndErrorQueue() {
		final String uuid = "uuid";
		final String table = "person";
		SiteInfo mockSite = Mockito.mock(SiteInfo.class);
		SiteReconciliation mockSiteRec = Mockito.mock(SiteReconciliation.class);
		ReceiverTableReconciliation mockTableRec = Mockito.mock(ReceiverTableReconciliation.class);
		when(mockSiteRecRepo.getBySite(mockSite)).thenReturn(mockSiteRec);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(mockSiteRec, table)).thenReturn(mockTableRec);
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setSite(mockSite);
		msg.setTableName(table);
		msg.setBatchSize(1);
		List<String> classnames = Utils.getListOfModelClassHierarchy(PersonModel.class.getName());
		when(mockSyncMsgRepo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, classnames, OPERATIONS))
		        .thenReturn(true);
		when(mockRetryRepo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, classnames, OPERATIONS))
		        .thenReturn(true);
		
		service.updateReconciliationMessage(msg, false, List.of(uuid));
		
		ArgumentCaptor<MissingEntity> argCaptor = ArgumentCaptor.forClass(MissingEntity.class);
		Mockito.verify(mockMissingRepo).save(argCaptor.capture());
		Assert.assertTrue(argCaptor.getValue().isInSyncQueue());
		Assert.assertTrue(argCaptor.getValue().isInErrorQueue());
		Mockito.verify(mockRecMsgRep).save(msg);
		Mockito.verifyNoInteractions(mockRequestRepo);
	}
	
	@Test
	public void updateReconciliationMessage_shouldNotSetTableProcessedCountToGoBeyondRowCount() {
		final String uuid = "uuid";
		final String table = "person";
		final long rowCount = 5;
		SiteInfo mockSite = Mockito.mock(SiteInfo.class);
		SiteReconciliation mockSiteRec = Mockito.mock(SiteReconciliation.class);
		ReceiverTableReconciliation tableRec = new ReceiverTableReconciliation();
		tableRec.setRowCount(rowCount);
		tableRec.setProcessedCount(rowCount);
		when(mockSiteRecRepo.getBySite(mockSite)).thenReturn(mockSiteRec);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(mockSiteRec, table)).thenReturn(tableRec);
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setSite(mockSite);
		msg.setTableName(table);
		msg.setBatchSize(1);
		
		service.updateReconciliationMessage(msg, false, List.of(uuid));
		
		Assert.assertEquals(rowCount, tableRec.getProcessedCount());
		Mockito.verify(mockTableRecRepo).save(tableRec);
	}
	
	@Test
	public void processJmsMessage_shouldNotAddDuplicateTableReconciliation() {
		final String siteId = "site-id";
		final String table = "person";
		JmsMessage jmsMsg = new JmsMessage();
		when(mockSiteRepo.getByIdentifier(siteId)).thenReturn(mockSite);
		jmsMsg.setSiteId(siteId);
		ReconciliationResponse resp = new ReconciliationResponse();
		resp.setTableName(table);
		resp.setBatchSize(0);
		resp.setData("");
		resp.setRowCount(10L);
		resp.setRemoteStartDate(LocalDateTime.now());
		jmsMsg.setBody(JsonUtils.marshalToBytes(resp));
		SiteReconciliation mockSiteRec = Mockito.mock(SiteReconciliation.class);
		when(mockSiteRecRepo.getBySite(mockSite)).thenReturn(mockSiteRec);
		ReceiverTableReconciliation mockTableRec = Mockito.mock(ReceiverTableReconciliation.class);
		when(mockTableRecRepo.getBySiteReconciliationAndTableName(mockSiteRec, table)).thenReturn(mockTableRec);
		
		service.processJmsMessage(jmsMsg);
		
		Mockito.verify(mockRecMsgRep).save(any(ReconciliationMessage.class));
		Mockito.verify(mockTableRecRepo, never()).save(any(ReceiverTableReconciliation.class));
		Mockito.verify(mockJmsMsgRepo).delete(jmsMsg);
	}
	
}
