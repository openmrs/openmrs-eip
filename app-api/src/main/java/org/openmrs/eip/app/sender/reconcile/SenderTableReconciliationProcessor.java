package org.openmrs.eip.app.sender.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.DEFAULT_LARGE_MSG_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_LARGE_MSG_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_RECONCILE_MSG_BATCH_SIZE;
import static org.openmrs.eip.app.SyncConstants.RECONCILE_MSG_BATCH_SIZE;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
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
public class SenderTableReconciliationProcessor extends BasePureParallelQueueProcessor<SenderTableReconciliation> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SenderTableReconciliationProcessor.class);
	
	@Value("${" + PROP_LARGE_MSG_SIZE + ":" + DEFAULT_LARGE_MSG_SIZE + "}")
	private int largeMsgSize;
	
	private SenderTableReconcileRepository tableReconcileRepo;
	
	private SenderReconcileRepository reconcileRepo;
	
	private JmsTemplate jmsTemplate;
	
	private Pageable page;
	
	public SenderTableReconciliationProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
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
		List<String> uuids = repo.getUuidBatchToReconcile(rec.getLastProcessedId(), rec.getEndId(), page);
		ReconciliationResponse response = new ReconciliationResponse();
		response.setTableName(rec.getTableName());
		response.setIdentifier(reconcileRepo.getReconciliation().getIdentifier());
		if (rec.getLastProcessedId() == 0) {
			//This is the first table payload to send
			response.setRemoteStartDate(rec.getStartDate());
			response.setRowCount(rec.getRowCount());
		}
		
		response.setBatchSize(uuids.size());
		response.setData(StringUtils.join(uuids, SyncConstants.RECONCILE_MSG_SEPARATOR));
		if (LOG.isTraceEnabled()) {
			LOG.debug("Send reconcile batch of {} rows in table {}, with id greater than {} up to {}", uuids.size(),
			    rec.getTableName(), rec.getLastProcessedId(), rec.getEndId());
		}
	}
	
}
