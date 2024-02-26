package org.openmrs.eip.app.receiver.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.PROP_MAX_BATCH_RECONCILE_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_MIN_BATCH_RECONCILE_SIZE;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.service.ReceiverReconcileService;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a ReconciliationMessage
 */
@Component("reconcileMsgProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReconcileMessageProcessor extends BaseQueueProcessor<ReconciliationMessage> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReconcileMessageProcessor.class);
	
	private final static int DEFAULT_MIN_BATCH_RECONCILE_SIZE = 50;
	
	private final static int DEFAULT_MAX_BATCH_RECONCILE_SIZE = 500;
	
	@Value("${" + PROP_MIN_BATCH_RECONCILE_SIZE + ":" + DEFAULT_MIN_BATCH_RECONCILE_SIZE + "}")
	private int minReconcileBatchSize;
	
	@Value("${" + PROP_MAX_BATCH_RECONCILE_SIZE + ":" + DEFAULT_MAX_BATCH_RECONCILE_SIZE + "}")
	private int maxReconcileBatchSize;
	
	private ReceiverReconcileService service;
	
	public ReconcileMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    ReceiverReconcileService service) {
		super(executor);
		this.service = service;
	}
	
	@Override
	public String getProcessorName() {
		return "reconcile msg";
	}
	
	@Override
	public String getQueueName() {
		return "reconcile-msg";
	}
	
	@Override
	public String getThreadName(ReconciliationMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public String getUniqueId(ReconciliationMessage item) {
		//Items belonging to same site and table are processed serially.
		return item.getSite().getIdentifier();
	}
	
	@Override
	public String getLogicalType(ReconciliationMessage item) {
		return item.getTableName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfTablesInHierarchy(logicalType);
	}
	
	@Override
	public void processItem(ReconciliationMessage msg) {
		String[] uuids = StringUtils.split(msg.getData().trim(), SyncConstants.RECONCILE_MSG_SEPARATOR);
		if (uuids.length != msg.getBatchSize()) {
			throw new EIPException("Batch size and item count do not for the reconciliation message");
		}
		
		//Pick up from where we left off
		if (msg.getProcessedCount() > 0) {
			uuids = Arrays.copyOfRange(uuids, msg.getProcessedCount(), uuids.length);
		}
		
		OpenmrsRepository repo = SyncContext.getRepositoryBean(msg.getTableName());
		List<String> uuidList = Arrays.stream(uuids).toList();
		reconcile(uuidList, uuidList, msg, repo);
	}
	
	private void reconcile(List<String> uuids, List<String> allUuids, ReconciliationMessage msg, OpenmrsRepository repo) {
		final int size = uuids.size();
		if (size > maxReconcileBatchSize) {
			bisectAndReconcile(uuids, allUuids, msg, repo);
			return;
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Reconciling batch of {} items from index {} to {} : ", size, allUuids.indexOf(uuids.get(0)),
			    allUuids.indexOf(uuids.get(size - 1)));
		}
		
		final int matchCount = repo.countByUuidIn(uuids);
		if (matchCount == 0 || matchCount == size) {
			boolean found = matchCount == size;
			if (LOG.isTraceEnabled()) {
				LOG.trace("Updating reconciliation msg with {} {} uuid(s) in table {}", size, (found ? "found" : "missing"),
				    msg.getTableName());
			}
			
			//All uuids are missing or existing
			service.updateReconciliationMessage(msg, found, uuids);
			return;
		}
		
		//Give up on the split approach and process uuids individually
		if (size < minReconcileBatchSize) {
			for (String uuid : uuids) {
				boolean found = repo.existsByUuid(uuid);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Updating reconciliation after {} uuid in table {}", (found ? "found" : "missing"),
					    msg.getTableName());
				}
				
				service.updateReconciliationMessage(msg, found, List.of(uuid));
			}
			
			return;
		}
		
		//Recursively split and check until we find a left half with no missing uuids and then proceed to the right.
		bisectAndReconcile(uuids, allUuids, msg, repo);
	}
	
	private void bisectAndReconcile(List<String> uuids, List<String> allUuids, ReconciliationMessage msg,
	                                OpenmrsRepository repo) {
		int midIndex = uuids.size() / 2;
		List<String> left = uuids.subList(0, midIndex);
		List<String> right = uuids.subList(midIndex, uuids.size());
		reconcile(left, allUuids, msg, repo);
		reconcile(right, allUuids, msg, repo);
	}
	
}
