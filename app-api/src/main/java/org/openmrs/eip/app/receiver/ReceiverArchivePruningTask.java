package org.openmrs.eip.app.receiver;

import java.util.Date;
import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.DateUtils;

/**
 * Reads a batch of sync archives that are older than a specific age in days and forwards them to
 * the {@link ReceiverArchivePruningProcessor}.
 */
public class ReceiverArchivePruningTask extends BaseReceiverSyncPrioritizingTask<ReceiverSyncArchive, ReceiverArchivePruningProcessor> {
	
	private ReceiverSyncArchiveRepository repo;
	
	private int maxAgeDays;
	
	public ReceiverArchivePruningTask(int maxAgeDays) {
		super(SyncContext.getBean(ReceiverArchivePruningProcessor.class));
		this.maxAgeDays = maxAgeDays;
		this.repo = SyncContext.getBean(ReceiverSyncArchiveRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "prune task";
	}
	
	@Override
	public List<ReceiverSyncArchive> getNextBatch() {
		Date maxDateCreated = DateUtils.subtractDays(new Date(), maxAgeDays);
		log.info("Pruning sync archives created on or before: " + maxDateCreated);
		
		return repo.findByDateCreatedLessThanEqual(maxDateCreated, AppUtils.getTaskPage());
	}
	
}
