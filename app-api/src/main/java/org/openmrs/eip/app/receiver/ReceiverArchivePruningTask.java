package org.openmrs.eip.app.receiver;

import static java.time.ZoneId.systemDefault;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_ARCHIVES_MAX_AGE_DAYS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRUNER_ENABLED;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.springframework.core.env.Environment;

public class ReceiverArchivePruningTask extends BaseReceiverSyncPrioritizingTask<ReceiverSyncArchive, ReceiverArchivePruningProcessor> {
	
	private ReceiverSyncArchiveRepository repo;
	
	private static Date maxDateCreated;
	
	public ReceiverArchivePruningTask() {
		super(SyncContext.getBean(ReceiverArchivePruningProcessor.class));
		this.repo = SyncContext.getBean(ReceiverSyncArchiveRepository.class);
		Environment e = SyncContext.getBean(Environment.class);
		Integer days = e.getProperty(PROP_ARCHIVES_MAX_AGE_DAYS, Integer.class);
		if (days == null) {
			throw new EIPException(
			        PROP_ARCHIVES_MAX_AGE_DAYS + " is required when " + PROP_PRUNER_ENABLED + "is set to true");
		}
		
		maxDateCreated = getMaxDateCreated(LocalDateTime.now(), days);
		
		log.info("Pruning sync archives older than: " + maxDateCreated);
	}
	
	@Override
	public String getTaskName() {
		return "prune task";
	}
	
	@Override
	public List<ReceiverSyncArchive> getNextBatch() {
		return repo.findByDateCreatedLessThanEqual(maxDateCreated, AppUtils.getTaskPage());
	}
	
	/**
	 * Computes the maximum date created based on the specified as of date value
	 * 
	 * @param asOfLocalDate local date time to use as current date
	 * @param maxAgeInDays maximum age in days
	 * @return Date maximum date created
	 */
	protected Date getMaxDateCreated(LocalDateTime asOfLocalDate, int maxAgeInDays) {
		LocalDateTime maxLocalDateTime = asOfLocalDate.minusDays(maxAgeInDays);
		maxLocalDateTime.toLocalDate().atTime(LocalTime.of(23, 59, 59));
		return Date.from(maxLocalDateTime.atZone(systemDefault()).toInstant());
	}
	
}
