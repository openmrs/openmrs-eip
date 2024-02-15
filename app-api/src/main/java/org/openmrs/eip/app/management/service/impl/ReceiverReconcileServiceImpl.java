package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.entity.receiver.TableReconciliation;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncRequestRepository;
import org.openmrs.eip.app.management.repository.ReconciliationMsgRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.TableReconciliationRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ReceiverReconcileService;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("receiverReconcileService")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverReconcileServiceImpl extends BaseService implements ReceiverReconcileService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverReconcileServiceImpl.class);
	
	private SiteRepository siteRepo;
	
	private ReconciliationMsgRepository reconcileMsgRep;
	
	private JmsMessageRepository jmsMsgRepo;
	
	private ReceiverSyncRequestRepository requestRepo;
	
	private SiteReconciliationRepository siteReconcileRepo;
	
	private TableReconciliationRepository tableReconcileRepo;
	
	public ReceiverReconcileServiceImpl(SiteRepository siteRepo, ReconciliationMsgRepository reconcileMsgRep,
	    JmsMessageRepository jmsMsgRepo, ReceiverSyncRequestRepository requestRepo,
	    SiteReconciliationRepository siteReconcileRepo, TableReconciliationRepository tableReconcileRepo) {
		this.siteRepo = siteRepo;
		this.reconcileMsgRep = reconcileMsgRep;
		this.jmsMsgRepo = jmsMsgRepo;
		this.requestRepo = requestRepo;
		this.siteReconcileRepo = siteReconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void processJmsMessage(JmsMessage jmsMessage) {
		ReconciliationMessage msg = new ReconciliationMessage();
		final SiteInfo site = siteRepo.getByIdentifier(jmsMessage.getSiteId());
		msg.setSite(site);
		ReconciliationResponse resp = JsonUtils.unmarshalBytes(jmsMessage.getBody(), ReconciliationResponse.class);
		final String table = resp.getTableName();
		msg.setTableName(table);
		msg.setBatchSize(resp.getBatchSize());
		msg.setLastTableBatch(resp.isLastTableBatch());
		msg.setData(resp.getData());
		msg.setDateCreated(new Date());
		if (LOG.isTraceEnabled()) {
			LOG.trace("Saving reconciliation message");
		}
		
		reconcileMsgRep.save(msg);
		
		if (resp.getRowCount() != null && resp.getRemoteStartDate() != null) {
			//These are the first uuids for the associated table
			if (LOG.isDebugEnabled()) {
				LOG.debug("Adding table reconciliation");
			}
			
			SiteReconciliation siteRec = siteReconcileRepo.getBySite(site);
			TableReconciliation tableRec = new TableReconciliation();
			tableRec.setTableName(table);
			tableRec.setRowCount(resp.getRowCount());
			tableRec.setRemoteStartDate(resp.getRemoteStartDate());
			tableRec.setLastBatchReceived(resp.isLastTableBatch());
			tableRec.setDateCreated(new Date());
			siteRec.addTableReconciliation(tableRec);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Saving updated to site reconciliation");
			}
			
			siteReconcileRepo.save(siteRec);
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Removing reconciliation message");
		}
		
		jmsMsgRepo.delete(jmsMessage);
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void updateReconciliationMessage(ReconciliationMessage message, boolean found, List<String> uuids) {
		if (!found) {
			for (String uuid : uuids) {
				ReceiverSyncRequest request = new ReceiverSyncRequest();
				request.setSite(message.getSite());
				request.setTableName(message.getTableName());
				request.setIdentifier(uuid);
				request.setRequestUuid(UUID.randomUUID().toString());
				request.setDateCreated(new Date());
				requestRepo.save(request);
			}
		}
		
		message.setProcessedCount(message.getProcessedCount() + uuids.size());
		if (LOG.isTraceEnabled()) {
			LOG.trace("Saving updated reconciliation message");
		}
		
		reconcileMsgRep.save(message);
		updateTableReconciliation(message, uuids.size());
	}
	
	/**
	 * Inserts or updates a table reconciliation based on the state of the specified message and
	 * processed count. Implementation of this method assumes no parallel invocations from multiple
	 * threads for reconciliation messages for the same site table.
	 *
	 * @param message the ReconciliationMessage instance
	 * @param processedUuidCount the count of processed uuids.
	 */
	private void updateTableReconciliation(ReconciliationMessage message, int processedUuidCount) {
		SiteReconciliation siteRec = siteReconcileRepo.getBySite(message.getSite());
		TableReconciliation tableRec = tableReconcileRepo.getBySiteReconciliationAndTableName(siteRec,
		    message.getTableName());
		tableRec.setProcessedCount(tableRec.getProcessedCount() + processedUuidCount);
		if (message.isLastTableBatch()) {
			tableRec.setLastBatchReceived(true);
		}
		tableRec.setDateChanged(LocalDateTime.now());
		if (message.isCompleted() && tableRec.isLastBatchReceived()
		        && tableRec.getRowCount() == tableRec.getProcessedCount()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Table reconciliation completed");
			}
			
			tableRec.setCompleted(true);
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Saving updated table reconciliation");
		}
		
		tableReconcileRepo.save(tableRec);
	}
	
}
