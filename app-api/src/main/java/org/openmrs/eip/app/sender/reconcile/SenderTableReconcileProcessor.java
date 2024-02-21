package org.openmrs.eip.app.sender.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_LARGE_MSG_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_LARGE_MSG_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_RECONCILE_MSG_BATCH_SIZE;
import static org.openmrs.eip.app.SyncConstants.RECONCILE_MSG_BATCH_SIZE;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.sender.SenderUtils;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.repository.OpenmrsRepository;
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
	
	private SenderTableReconcileRepository tableReconcileRepo;
	
	private SenderReconcileRepository reconcileRepo;
	
	private JmsTemplate jmsTemplate;
	
	private Pageable page;
	
	public SenderTableReconcileProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    @Value("${" + PROP_RECONCILE_MSG_BATCH_SIZE + ":" + RECONCILE_MSG_BATCH_SIZE + "}") int batchSize,
	    SenderTableReconcileRepository tableReconcileRepo, SenderReconcileRepository reconcileRepo,
	    JmsTemplate jmsTemplate) {
		super(executor);
		this.page = Pageable.ofSize(batchSize);
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
		List<Object[]> batch = repo.getUuidAndIdBatchToReconcile(rec.getLastProcessedId(), rec.getEndId(), page);
		final String table = rec.getTableName();
		ReconciliationResponse response = new ReconciliationResponse();
		response.setIdentifier(reconcileRepo.getReconciliation().getIdentifier());
		response.setTableName(table);
		if (!rec.isStarted()) {
			//This is the first table payload to send
			response.setRemoteStartDate(rec.getSnapshotDate());
			response.setRowCount(rec.getRowCount());
		}
		
		List<String> uuids = batch.stream().map(entry -> entry[0].toString()).collect(Collectors.toList());
		Long firstId;
		Long lastId;
		boolean lastBatch;
		if (batch.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("No more rows to reconcile");
			}
			
			lastBatch = true;
			lastId = rec.getEndId();
		} else {
			firstId = (Long) batch.get(0)[1];
			lastId = (Long) batch.get(batch.size() - 1)[1];
			lastBatch = lastId == rec.getEndId();
			if (LOG.isTraceEnabled()) {
				LOG.debug("Sending reconcile batch of {} rows in table {}, with ids from {} up to {}", uuids.size(), table,
				    firstId, lastId);
			}
		}
		
		response.setData(StringUtils.join(uuids, SyncConstants.RECONCILE_MSG_SEPARATOR));
		response.setBatchSize(uuids.size());
		response.setLastTableBatch(lastBatch);
		//TODO First compress payload if necessary
		jmsTemplate.convertAndSend(SenderUtils.getQueueName(), response);
		
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
