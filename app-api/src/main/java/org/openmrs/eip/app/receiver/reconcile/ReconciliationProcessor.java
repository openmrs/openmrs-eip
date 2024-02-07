package org.openmrs.eip.app.receiver.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.Reconciliation;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.repository.ReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.service.ReconcileService;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a Reconciliation item
 */
@Component("reconciliationProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReconciliationProcessor extends BasePureParallelQueueProcessor<Reconciliation> {
	
	protected static final Logger log = LoggerFactory.getLogger(ReconciliationProcessor.class);
	
	private ReconcileService service;
	
	private SiteRepository siteRepo;
	
	private ReconciliationRepository reconcileRepo;
	
	private SiteReconciliationRepository siteReconcileRepo;
	
	public ReconciliationProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, ReconcileService service,
	    SiteRepository siteRepo, ReconciliationRepository reconcileRepo, SiteReconciliationRepository siteReconcileRepo) {
		super(executor);
		this.service = service;
		this.siteRepo = siteRepo;
		this.reconcileRepo = reconcileRepo;
		this.siteReconcileRepo = siteReconcileRepo;
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
	public String getThreadName(Reconciliation item) {
		return item.getId().toString();
	}
	
	@Override
	public void processItem(Reconciliation reconciliation) {
		//If site has no reconcile item, insert one, if all sites are covered, marked as started
		if (reconciliation.isStarted()) {
			initialize(reconciliation);
		} else {
			update(reconciliation);
		}
	}
	
	private void initialize(Reconciliation reconciliation) {
		for (SiteInfo site : siteRepo.findAll()) {
			if (siteReconcileRepo.getBySite(site) == null) {
				//TODO First send a reconciliation request
				SiteReconciliation siteRec = new SiteReconciliation();
				siteRec.setSite(site);
				siteReconcileRepo.save(siteRec);
			}
		}
		
		reconciliation.setStarted(true);
		reconcileRepo.save(reconciliation);
	}
	
	private void update(Reconciliation reconciliation) {
		
	}
	
}
