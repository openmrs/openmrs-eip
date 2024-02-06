package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.PROP_MAX_BATCH_RECONCILE_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_MIN_BATCH_RECONCILE_SIZE;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.service.ReconcileService;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a receiver sync archive by moving it to the pruned queue.
 */
@Component("reconcileMsgProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReconciliationMessageProcessor extends BasePureParallelQueueProcessor<ReconciliationMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(ReconciliationMessageProcessor.class);
	
	private final static int DEFAULT_MIN_BATCH_RECONCILE_SIZE = 50;
	
	private final static int DEFAULT_MAX_BATCH_RECONCILE_SIZE = 500;
	
	@Value("${" + PROP_MIN_BATCH_RECONCILE_SIZE + ":" + DEFAULT_MIN_BATCH_RECONCILE_SIZE + "}")
	private long minReconcileBatchSize;
	
	@Value("${" + PROP_MAX_BATCH_RECONCILE_SIZE + ":" + DEFAULT_MAX_BATCH_RECONCILE_SIZE + "}")
	private long maxReconcileBatchSize;
	
	private ReconcileService service;
	
	public ReconciliationMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    ReconcileService service) {
		super(executor);
		this.service = service;
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
	public String getThreadName(ReconciliationMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public void processItem(ReconciliationMessage msg) {
		String[] uuids = StringUtils.split(msg.getData().trim(), SyncConstants.RECONCILE_MSG_SEPARATOR);
		if (uuids.length != msg.getBatchSize()) {
			throw new EIPException("Batch size and item count do not for the reconciliation message");
		}
		
		OpenmrsRepository repo = SyncContext.getRepositoryBean(msg.getTableName());
		List<String> uuidList = Arrays.stream(uuids).toList();
		reconcile(uuidList, uuidList, msg, repo);
	}
	
	private void reconcile(List<String> uuids, List<String> allUuids, ReconciliationMessage msg, OpenmrsRepository repo) {
		final int size = uuids.size();
		if (log.isTraceEnabled()) {
			log.trace("Reconciling batch of {} items from index {} to {} : ", size, allUuids.indexOf(uuids.get(0)),
			    allUuids.indexOf(uuids.get(size - 1)));
		}
		
		if (size > maxReconcileBatchSize) {
			bisectAndReconcile(uuids, allUuids, msg, repo);
			return;
		}
		
		final int matchCount = repo.countByUuidIn(uuids);
		if (matchCount == 0 || matchCount == size) {
			boolean found = matchCount == size;
			if (log.isTraceEnabled()) {
				log.trace("Updating reconciliation msg with {} {} uuid(s)", matchCount, (found ? "found" : "missing"));
			}
			
			//All uuids are missing or existing
			service.updateReconciliationMessage(msg, found, uuids);
			return;
		}
		
		//Give up on the split approach and process uuids individually
		if (size < minReconcileBatchSize) {
			for (String uuid : uuids) {
				boolean found = repo.existsByUuid(uuid);
				if (log.isTraceEnabled()) {
					log.trace("Updating reconciliation msg for {} uuid", (found ? "found" : "missing"));
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
