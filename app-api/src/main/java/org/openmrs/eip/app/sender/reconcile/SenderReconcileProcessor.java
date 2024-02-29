package org.openmrs.eip.app.sender.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.sender.DeletedEntity;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.DeletedEntityRepository;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.management.service.SenderReconcileService;
import org.openmrs.eip.app.sender.SenderConstants;
import org.openmrs.eip.app.sender.SenderUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Processes a SenderReconciliation item
 */
@Component("senderReconcileProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderReconcileProcessor extends BasePureParallelQueueProcessor<SenderReconciliation> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SenderReconcileProcessor.class);
	
	private SenderReconcileRepository reconcileRepo;
	
	private SenderTableReconcileRepository tableReconcileRepo;
	
	private DeletedEntityRepository deleteRepo;
	
	private SenderReconcileService service;
	
	private JmsTemplate jmsTemplate;
	
	@Value("${" + SenderConstants.PROP_SENDER_ID + "}")
	private String siteId;
	
	public SenderReconcileProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SenderReconcileRepository reconcileRepo, SenderTableReconcileRepository tableReconcileRepo,
	    DeletedEntityRepository deleteRepo, SenderReconcileService service, JmsTemplate jmsTemplate) {
		super(executor);
		this.reconcileRepo = reconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
		this.deleteRepo = deleteRepo;
		this.service = service;
		this.jmsTemplate = jmsTemplate;
	}
	
	@Override
	public String getProcessorName() {
		return "reconcile";
	}
	
	@Override
	public String getQueueName() {
		return "reconcile";
	}
	
	@Override
	public String getThreadName(SenderReconciliation item) {
		return item.getId().toString();
	}
	
	@Override
	public void processItem(SenderReconciliation reconciliation) {
		switch (reconciliation.getStatus()) {
			case NEW:
				initialize(reconciliation);
				break;
			case PROCESSING:
				process(reconciliation);
				break;
			case POST_PROCESSING:
				postProcess(reconciliation);
				break;
			case COMPLETED:
				throw new EIPException("Reconciliation is already completed");
		}
	}
	
	private void initialize(SenderReconciliation reconciliation) {
		List<SenderTableReconciliation> snapshots = service.takeSnapshot();
		service.saveSnapshot(reconciliation, snapshots);
	}
	
	private void process(SenderReconciliation reconciliation) {
		List<String> incompleteTables = AppUtils.getTablesToSync().stream()
		        .filter(t -> !tableReconcileRepo.getByTableNameIgnoreCase(t).isCompleted()).toList();
		if (incompleteTables.isEmpty()) {
			reconciliation.setStatus(SenderReconcileStatus.POST_PROCESSING);
			LOG.info("Updating reconciliation status to " + reconciliation.getStatus());
			if (LOG.isTraceEnabled()) {
				LOG.debug("Saving updated reconciliation");
			}
			
			reconcileRepo.save(reconciliation);
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("There is still {} incomplete table reconciliation(s)", incompleteTables.size());
			}
		}
	}
	
	private void postProcess(SenderReconciliation rec) {
		sendDeletedUuids(rec);
		sendAllDeletedUuids(rec);
	}
	
	protected void sendDeletedUuids(SenderReconciliation rec) {
		Date date = rec.getDateCreated();
		tableReconcileRepo.findAll().forEach(tableRec -> {
			final String table = tableRec.getTableName();
			List<DeletedEntity> deletes = deleteRepo.getByTableNameIgnoreCaseAndDateCreatedGreaterThanEqual(table, date);
			List<String> deletedUuids = new ArrayList<>(deletes.size());
			for (DeletedEntity del : deletes) {
				Long id = Long.valueOf(del.getPrimaryKeyId());
				if (id <= tableRec.getEndId()) {
					String uuid;
					if (!Utils.isSubclassTable(table)) {
						uuid = del.getIdentifier();
					} else {
						uuid = getUuidFromParent(table, id);
						if (StringUtils.isBlank(uuid)) {
							continue;
						}
					}
					
					deletedUuids.add(uuid);
				}
			}
			
			if (!deletedUuids.isEmpty()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Sending {} uuids for entities deleted during reconciliation from {} table",
					    deletedUuids.size(), table);
				}
				
				send(rec, table, deletedUuids, false);
			}
		});
	}
	
	protected void sendAllDeletedUuids(SenderReconciliation rec) {
		AppUtils.getTablesToSync().forEach(table -> {
			List<DeletedEntity> delEntities = deleteRepo.getByTableNameIgnoreCase(table);
			List<String> uuids = new ArrayList<>(delEntities.size());
			for (DeletedEntity del : delEntities) {
				String uuid;
				if (!Utils.isSubclassTable(table)) {
					uuid = del.getIdentifier();
				} else {
					uuid = getUuidFromParent(table, Long.valueOf(del.getPrimaryKeyId()));
					if (StringUtils.isBlank(uuid)) {
						continue;
					}
				}
				
				uuids.add(uuid);
			}
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Sending {} uuids for all entities deleted from {} table", uuids.size(), table);
			}
			
			send(rec, table, uuids, true);
		});
	}
	
	protected void send(SenderReconciliation rec, String table, List<String> uuids, boolean lastBatch) {
		ReconciliationResponse response = new ReconciliationResponse();
		response.setIdentifier(rec.getIdentifier());
		response.setTableName(table);
		response.setLastTableBatch(lastBatch);
		//TODO If size is larger than reconcile batch size break up the uuids
		response.setBatchSize(uuids.size());
		response.setData(StringUtils.join(uuids, SyncConstants.RECONCILE_MSG_SEPARATOR));
		
		final String json = JsonUtils.marshall(response);
		//TODO To avoid message duplication, add message to outbound queue e.g. this can happen if message is sent
		//but status not update and uuids are resent
		jmsTemplate.send(SenderUtils.getQueueName(), new ReconcileResponseCreator(json, siteId));
	}
	
	private String getUuidFromParent(String table, Long id) {
		String uuid = SenderUtils.getUuidFromParentTable(table, id);
		if (StringUtils.isBlank(uuid)) {
			DeletedEntity delEntity = deleteRepo.getByTableNameIgnoreCaseAndPrimaryKeyId(table, id.toString());
			if (delEntity == null) {
				LOG.warn("Failed to resolve uuid for delete entity");
				return null;
			}
			uuid = delEntity.getIdentifier();
		}
		
		return uuid;
	}
	
}
