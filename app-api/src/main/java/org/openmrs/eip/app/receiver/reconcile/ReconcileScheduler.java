package org.openmrs.eip.app.receiver.reconcile;

import java.util.Date;
import java.util.UUID;

import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class ReconcileScheduler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReconcileScheduler.class);
	
	private ReceiverReconcileRepository repo;
	
	public ReconcileScheduler(ReceiverReconcileRepository repo) {
		this.repo = repo;
	}
	
	@Scheduled(cron = "${reconcile.schedule.cron:-}")
	public void execute() {
		LOG.info("Initiating new reconciliation");
		ReceiverReconciliation rec = new ReceiverReconciliation();
		rec.setIdentifier(UUID.randomUUID().toString());
		rec.setDateCreated(new Date());
		if (LOG.isDebugEnabled()) {
			LOG.debug("Saving new reconciliation");
		}
		
		repo.save(rec);
	}
	
}
