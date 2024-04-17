package org.openmrs.eip.app.receiver.reconcile;

import org.openmrs.eip.app.management.service.ReceiverReconcileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class ReconcileScheduler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReconcileScheduler.class);
	
	private ReceiverReconcileService service;
	
	public ReconcileScheduler(ReceiverReconcileService service) {
		this.service = service;
	}
	
	@Scheduled(cron = "${reconcile.schedule.cron:-}")
	public void execute() {
		LOG.info("Adding new reconciliation");
		service.addNewReconciliation();
	}
	
}
