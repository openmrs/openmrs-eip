package org.openmrs.eip.app.sender.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_LARGE_MSG_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_LARGE_MSG_SIZE;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.sender.SenderConstants;
import org.openmrs.eip.app.sender.SenderUtils;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Processes a SenderTableReconciliation item
 */
@Component("senderTableReconcileProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderTableReconcileProcessor extends BasePureParallelQueueProcessor<SenderTableReconciliation> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SenderTableReconcileProcessor.class);
	
	@Value("${" + PROP_LARGE_MSG_SIZE + ":" + DEFAULT_LARGE_MSG_SIZE + "}")
	private int largeMsgSize;
	
	@Value("${" + SenderConstants.PROP_SENDER_ID + "}")
	private String siteId;
	
	private SenderTableReconcileRepository tableReconcileRepo;
	
	private SenderReconcileRepository reconcileRepo;
	
	private JmsTemplate jmsTemplate;
	
	public SenderTableReconcileProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SenderTableReconcileRepository tableReconcileRepo, SenderReconcileRepository reconcileRepo,
	    JmsTemplate jmsTemplate) {
		super(executor);
		this.tableReconcileRepo = tableReconcileRepo;
		this.reconcileRepo = reconcileRepo;
		this.jmsTemplate = jmsTemplate;
	}
	
	@Override
	public String getProcessorName() {
		return "table reconcile";
	}
	
	@Override
	public String getQueueName() {
		return "table reconcile";
	}
	
	@Override
	public String getThreadName(SenderTableReconciliation item) {
		return item.getId().toString();
	}
	
	@Override
	public void processItem(SenderTableReconciliation rec) {
		OpenmrsRepository<?> repo = SyncContext.getRepositoryBean(rec.getTableName());
		SenderReconciliation senderRec = reconcileRepo.getReconciliation();
		final Pageable page = Pageable.ofSize(senderRec.getBatchSize());
		List<Object[]> batch = repo.getIdAndUuidBatchToReconcile(rec.getLastProcessedId(), rec.getEndId(), page);
		final String table = rec.getTableName();
		ReconciliationResponse response = new ReconciliationResponse();
		response.setIdentifier(senderRec.getIdentifier());
		response.setTableName(table);
		if (!rec.isStarted()) {
			//This is the first table payload to send
			response.setRemoteStartDate(rec.getSnapshotDate());
			response.setRowCount(rec.getRowCount());
		}
		
		List<String> uuids = batch.stream().map(entry -> entry[1].toString()).collect(Collectors.toList());
		Long lastId;
		if (batch.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No more rows to reconcile in table: {}", table);
			}
			//Don't set lastBatch here, it is set in POST_PROCESSING STEP
			lastId = rec.getEndId();
		} else {
			List<Long> ids = batch.stream().map(entry -> (Long) entry[0]).collect(Collectors.toList());
			Long firstId = ids.get(0);
			lastId = ids.get(ids.size() - 1);
			if (LOG.isTraceEnabled()) {
				LOG.debug("Sending reconcile batch of {} rows in table {}, with ids from {} up to {}", uuids.size(), table,
				    firstId, lastId);
			}
		}
		
		response.setData(StringUtils.join(uuids, SyncConstants.RECONCILE_MSG_SEPARATOR));
		response.setBatchSize(uuids.size());
		final String json = JsonUtils.marshall(response);
		//TODO To avoid message duplication, add message to outbound queue e.g. this can happen if message is sent 
		//but status not update and uuids are resent
		jmsTemplate.send(SenderUtils.getQueueName(), new ReconcileResponseCreator(json, siteId));
		
		if (LOG.isTraceEnabled()) {
			LOG.debug("Updating last processed id of table {} to {}", table, lastId);
		}
		
		rec.setLastProcessedId(lastId);
		if (!rec.isStarted()) {
			rec.setStarted(true);
		}
		
		tableReconcileRepo.save(rec);
	}
	
}
