package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.ArrayList;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a receiver sync archive by moving it to the pruned queue.
 */
@Component("reconcileMsgProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReconciliationMessageProcessor extends BasePureParallelQueueProcessor<ReconciliationMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(ReconciliationMessageProcessor.class);
	
	private final static int MIN_PROCESS_SIZE = 50;
	
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
	public void processItem(ReconciliationMessage item) {
		//TODO Mark entities as reconciled
		String[] uuids = StringUtils.split(item.getData().trim(), SyncConstants.RECONCILE_MSG_SEPARATOR);
		if (uuids.length != item.getBatchSize()) {
			throw new EIPException("Batch size and item count do not for the reconciliation message");
		}
		
		OpenmrsRepository repo = SyncContext.getRepositoryBean(item.getTableName());
		int maxProcessSize = SyncConstants.RECONCILE_MSG_BATCH_SIZE / 2;
		if (item.getBatchSize() < maxProcessSize) {
			reconcile(uuids, repo);
		} else {
			int midIndex = item.getBatchSize() / 2;
			reconcile(Arrays.copyOfRange(uuids, 0, midIndex), repo);
			reconcile(Arrays.copyOfRange(uuids, midIndex, item.getBatchSize()), repo);
		}
	}
	
	private void reconcile(String[] uuids, OpenmrsRepository repo) {
		final int size = uuids.length;
		if (repo.countByUuidIn(uuids) == uuids.length) {
			//Mark all as found and update processed count
			return;
		}
		
		if (size <= MIN_PROCESS_SIZE) {
			List<String> foundUuids = new ArrayList<>(uuids.length);
			for (String uuid : uuids) {
				if (repo.existsByUuid(uuid)) {
					foundUuids.add(uuid);
				} else {
					//TODO Add to sync request queue
				}
			}
			
			//TODO Mark all in foundUuids as found
			//TODO update processed count
			return;
		}
		
		splitAndReconcile(uuids, repo);
	}
	
	private void splitAndReconcile(String[] uuids, OpenmrsRepository repo) {
		int midIndex = uuids.length / 2;
		reconcile(Arrays.copyOfRange(uuids, 0, midIndex), repo);
		reconcile(Arrays.copyOfRange(uuids, midIndex, uuids.length), repo);
	}
	
}
