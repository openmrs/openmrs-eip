package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.component.SyncOperation.c;
import static org.openmrs.eip.component.SyncOperation.r;
import static org.openmrs.eip.component.SyncOperation.s;
import static org.openmrs.eip.component.SyncOperation.u;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.MissingEntity;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncRequest;
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
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ReceiverReconcileService;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("receiverReconcileService")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverReconcileServiceImpl extends BaseService implements ReceiverReconcileService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverReconcileServiceImpl.class);
	
	protected static final List<SyncOperation> OPERATIONS = List.of(c, u, r, s);
	
	private SiteRepository siteRepo;
	
	private ReconciliationMsgRepository reconcileMsgRep;
	
	private JmsMessageRepository jmsMsgRepo;
	
	private ReceiverSyncRequestRepository requestRepo;
	
	private SiteReconciliationRepository siteReconcileRepo;
	
	private ReceiverTableReconcileRepository tableReconcileRepo;
	
	private MissingEntityRepository missingRepo;
	
	private SyncMessageRepository syncMsgRepo;
	
	private ReceiverRetryRepository retryRepo;
	
	public ReceiverReconcileServiceImpl(SiteRepository siteRepo, ReconciliationMsgRepository reconcileMsgRep,
	    JmsMessageRepository jmsMsgRepo, ReceiverSyncRequestRepository requestRepo,
	    SiteReconciliationRepository siteReconcileRepo, ReceiverTableReconcileRepository tableReconcileRepo,
	    MissingEntityRepository missingRepo, SyncMessageRepository syncMsgRepo, ReceiverRetryRepository retryRepo) {
		this.siteRepo = siteRepo;
		this.reconcileMsgRep = reconcileMsgRep;
		this.jmsMsgRepo = jmsMsgRepo;
		this.requestRepo = requestRepo;
		this.siteReconcileRepo = siteReconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
		this.missingRepo = missingRepo;
		this.syncMsgRepo = syncMsgRepo;
		this.retryRepo = retryRepo;
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
				LOG.debug("Adding reconciliation for table {}", table);
			}
			
			SiteReconciliation siteRec = siteReconcileRepo.getBySite(site);
			if (tableReconcileRepo.getBySiteReconciliationAndTableName(siteRec, table) != null) {
				//Typically, this would ONLY happen if duplicate payload was sent 
				if (LOG.isDebugEnabled()) {
					LOG.debug("Encountered possible duplicate initial reconcile payload for table {}", table);
				}
			} else {
				ReceiverTableReconciliation tableRec = new ReceiverTableReconciliation();
				tableRec.setSiteReconciliation(siteRec);
				tableRec.setTableName(table);
				tableRec.setRowCount(resp.getRowCount());
				tableRec.setRemoteStartDate(resp.getRemoteStartDate());
				tableRec.setLastBatchReceived(resp.isLastTableBatch());
				tableRec.setDateCreated(new Date());
				
				if (LOG.isTraceEnabled()) {
					LOG.trace("Saving reconciliation for table {}", table);
				}
				
				tableReconcileRepo.save(tableRec);
			}
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Removing reconciliation JMS message");
		}
		
		jmsMsgRepo.delete(jmsMessage);
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void updateReconciliationMessage(ReconciliationMessage message, boolean found, List<String> uuids) {
		if (!found) {
			final String table = message.getTableName();
			Class<? extends BaseModel> modelClass = TableToSyncEnum.getTableToSyncEnum(table.toUpperCase()).getModelClass();
			List<String> classNames = Utils.getListOfModelClassHierarchy(modelClass.getName());
			for (String uuid : uuids) {
				MissingEntity missing = new MissingEntity();
				missing.setIdentifier(uuid);
				missing.setTableName(table);
				missing.setSite(message.getSite());
				missing.setDateCreated(new Date());
				boolean inSync = retryRepo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, classNames, OPERATIONS);
				missing.setInErrorQueue(inSync);
				boolean inError = syncMsgRepo.existsByIdentifierAndModelClassNameInAndOperationIn(uuid, classNames,
				    OPERATIONS);
				missing.setInSyncQueue(inError);
				missingRepo.save(missing);
				
				if (!inSync && !inError) {
					ReceiverSyncRequest request = new ReceiverSyncRequest();
					request.setSite(message.getSite());
					request.setTableName(table);
					request.setIdentifier(uuid);
					request.setRequestUuid(UUID.randomUUID().toString());
					request.setDateCreated(new Date());
					requestRepo.save(request);
				}
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
		ReceiverTableReconciliation tableRec = tableReconcileRepo.getBySiteReconciliationAndTableName(siteRec,
		    message.getTableName());
		tableRec.setProcessedCount(tableRec.getProcessedCount() + processedUuidCount);
		if (tableRec.getProcessedCount() > tableRec.getRowCount()) {
			//This would ONLY happen if we received duplicate payloads.
			tableRec.setProcessedCount(tableRec.getRowCount());
		}
		
		if (message.isLastTableBatch()) {
			tableRec.setLastBatchReceived(true);
		}
		
		tableRec.setDateChanged(LocalDateTime.now());
		if (message.isCompleted()) {
			reconcileMsgRep.delete(message);
			//TODO check that tableRec.getRowCount() == tableRec.getProcessedCount() after we rule out duplicate
			//deliveries of reconciliation messages.
			if (tableRec.isLastBatchReceived()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Reconciliation completed for table {}", message.getTableName());
				}
				
				tableRec.setCompleted(true);
			}
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Saving updated table reconciliation");
		}
		
		tableReconcileRepo.save(tableRec);
	}
	
}
