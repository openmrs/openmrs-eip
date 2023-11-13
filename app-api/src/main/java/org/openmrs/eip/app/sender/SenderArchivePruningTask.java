package org.openmrs.eip.app.sender;

import java.util.Date;
import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.repository.SenderSyncArchiveRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.DateUtils;

/**
 * Reads a batch of sender sync archives that are older than a specific age in days and forwards
 * them to the {@link SenderArchivePruningProcessor}.
 */
public class SenderArchivePruningTask extends BaseDelegatingQueueTask<SenderSyncArchive, SenderArchivePruningProcessor> {
	
	private SenderSyncArchiveRepository repo;
	
	private int maxAgeDays;
	
	public SenderArchivePruningTask(int maxAgeDays) {
		super(SyncContext.getBean(SenderArchivePruningProcessor.class));
		this.maxAgeDays = maxAgeDays;
		this.repo = SyncContext.getBean(SenderSyncArchiveRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "prune task";
	}
	
	@Override
	public List<SenderSyncArchive> getNextBatch() {
		Date maxDateCreated = DateUtils.subtractDays(new Date(), maxAgeDays);
		log.info("Pruning sync archives created on or before: " + maxDateCreated);
		
		return repo.findByDateCreatedLessThanEqual(maxDateCreated, AppUtils.getTaskPage());
	}
	
}
