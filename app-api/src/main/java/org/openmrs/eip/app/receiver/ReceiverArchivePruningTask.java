package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_ARCHIVES_MAX_AGE_DAYS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRUNER_ENABLED;

import java.util.Date;
import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.DateUtils;
import org.springframework.core.env.Environment;

public class ReceiverArchivePruningTask extends BaseReceiverSyncPrioritizingTask<ReceiverSyncArchive, ReceiverArchivePruningProcessor> {
	
	private ReceiverSyncArchiveRepository repo;
	
	private static Integer maxAgeDays;
	
	public ReceiverArchivePruningTask() {
		super(SyncContext.getBean(ReceiverArchivePruningProcessor.class));
		this.repo = SyncContext.getBean(ReceiverSyncArchiveRepository.class);
		Environment e = SyncContext.getBean(Environment.class);
		maxAgeDays = e.getProperty(PROP_ARCHIVES_MAX_AGE_DAYS, Integer.class);
		if (maxAgeDays == null) {
			throw new EIPException(
			        PROP_ARCHIVES_MAX_AGE_DAYS + " is required when " + PROP_PRUNER_ENABLED + "is set to true");
		}
		
		log.info("Pruning sync archives older than " + maxAgeDays + " days");
	}
	
	@Override
	public String getTaskName() {
		return "prune task";
	}
	
	@Override
	public List<ReceiverSyncArchive> getNextBatch() {
		Date maxDateCreated = DateUtils.subtractDays(new Date(), maxAgeDays);
		if (log.isDebugEnabled()) {
			log.info("Pruning sync archives created on or before: " + maxDateCreated);
		}
		
		return repo.findByDateCreatedLessThanEqual(maxDateCreated, AppUtils.getTaskPage());
	}
	
}
